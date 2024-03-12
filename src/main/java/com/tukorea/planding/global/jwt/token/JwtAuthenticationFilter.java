package com.tukorea.planding.global.jwt.token;


import com.tukorea.planding.global.jwt.token.service.RefreshTokenService;
import com.tukorea.planding.global.jwt.token.service.TokenService;
import com.tukorea.planding.global.oauth.details.CustomUserDetailsService;
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
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }
        String accessToken = tokenService.extractAccessToken(request).orElse(null);
        String refreshToken = tokenService.extractRefreshToken(request).orElse(null);


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

        String email = tokenService.resolveSubject(accessToken);
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
        tokenService.validateToken(accessToken);
        log.debug("유효한 토큰입니다.");
    }

    private void checkRefreshTokenAndRegeneratedToken(String refreshToken,
                                                      HttpServletResponse response) {
        log.debug("Refresh 토큰 확인 및 검증");

        tokenService.validateToken(refreshToken);
        String email = tokenService.resolveSubject(refreshToken);

        String newAccessToken = tokenService.generateAccessToken(email);
        String newRefreshToken = tokenService.generateRefreshToken(email);

        tokenService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
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
}
