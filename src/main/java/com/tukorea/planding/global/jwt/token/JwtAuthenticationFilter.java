package com.tukorea.planding.global.jwt.token;


import com.tukorea.planding.global.jwt.token.service.RefreshTokenService;
import com.tukorea.planding.global.jwt.token.service.TokenService;
import com.tukorea.planding.global.oauth.details.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;


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

        // 헤더에서 추출한 token값이 맞는지 검증로직
        Authentication authentication = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 리프레시 토큰이 만료되었다면 새로운 액세스 ,리프레시 토큰 생성
        // 그렇지않으면 액세스토큰 검증 이후
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        String email = tokenService.resolveSubject(token);

        if (email != null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }
        return null;
    }

    private void checkAccessToken(String accessToken) {
        log.info("Access 토큰 확인 및 검증");
        tokenService.validateToken(accessToken);
        log.info("유효한 토큰입니다.");
    }

    private void checkRefreshTokenAndRegeneratedToken(String refreshToken,
                                                      HttpServletResponse response) {
        log.info("Refresh 토큰 확인 및 검증");

        tokenService.validateToken(refreshToken);
        String email = tokenService.resolveSubject(refreshToken);

        String newAccessToken = tokenService.generateAccessToken(email);
        String newRefreshToken = tokenService.generateRefreshToken(email);

        tokenService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
        refreshTokenService.updateRefreshToken(email, newRefreshToken);
    }
}
