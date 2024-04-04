package com.tukorea.planding.global.jwt.token;


import com.tukorea.planding.global.jwt.redis.RedisService;
import com.tukorea.planding.global.jwt.token.service.RefreshTokenService;
import com.tukorea.planding.global.jwt.token.service.JwtService;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import com.tukorea.planding.user.dto.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RedisService redisService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getRequestURI().startsWith("/api/v1/ws")) {
            log.info("웹소켓 인증이 필요하지 않는 API");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

        if (accessToken == null) {
            log.warn("엑세스 코드가 없는 요청");
        }

        // refreshToken이 존재시 refreshToken,accessToken 모두 업데이트 필요
        if (refreshToken != null) {
            checkRefreshTokenAndRegeneratedToken(refreshToken, response);
            return;
        }

        // accessToken 검증 진행, 액세스 토큰 오류 발생시 에러를 throw함
        if (refreshToken == null) {
            checkAccessToken(accessToken);
        }

        String email = jwtService.getEmailFromJwtToken(accessToken);
        log.info(email);
        saveAuthentication(userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다.")));


        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(UserInfo userInfo) {
        return new UsernamePasswordAuthenticationToken(userInfo, null, List.of(new SimpleGrantedAuthority(userInfo.getRole().getAuthority())));
    }

    private void checkAccessToken(String accessToken) {
        log.debug("Access 토큰 확인 및 검증");
        jwtService.validateToken(accessToken);
        log.debug("유효한 토큰입니다.");
    }

    private void checkRefreshTokenAndRegeneratedToken(String refreshToken,
                                                      HttpServletResponse response) {
        log.debug("Refresh 토큰 확인 및 검증");

        jwtService.validateToken(refreshToken);
        String email = jwtService.getEmailFromJwtToken(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
        refreshTokenService.updateRefreshToken(email, newRefreshToken);
    }

    private void saveAuthentication(User user) {
        log.info("Authentication context에 저장");
        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .code(user.getCode())
                .build();
        Authentication authentication = getAuthentication(userInfo);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePath = {
                "/oauth2/**", "/login", "/login/**", "/swagger-ui/**",
                "/v3/api-docs/**", "/swagger-ui/index.html",
                "/swagger-ui/swagger-ui-standalone-preset.js", "/swagger-ui/swagger-initializer.js",
                "/swagger-ui/swagger-ui-bundle.js", "/swagger-ui/swagger-ui.css",
                "/swagger-ui/index.css", "/swagger-ui/favicon-32x32.png",
                "/swagger-ui/favicon-16x16.png",
                "/api-docs/json/swagger-config", "/api-docs/json",
                "/v3/api-docs/swagger-config", "/v3/api-docs",
        };
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }
}
