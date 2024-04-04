package com.tukorea.planding.global.jwt.token.service;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private static final String BEARER = "Bearer ";

    /**
     * 토큰 생성 메서드
     *
     * @param expiration 만료시간 (액세스 or 리프레시)
     * @param email      사용자 이메일정보
     * @return 토큰
     */
    public String generateToken(long expiration, String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSECRET_KEY()) // HS512과 비교했을때 본 서비스는 암호화 난이도를 낮게 설정
                .compact();
    }

    public String generateAccessToken(final String email) {
        return generateToken(jwtProperties.getAccessExpiration(), email);
    }

    public String generateRefreshToken(final String email) {
        return generateToken(jwtProperties.getRefreshExpiration(), email);
    }

    /**
     * 토큰을 파싱해서 올바른 토큰인지 확인
     *
     * @param token 검증할 토큰
     * @return 유효한 토큰이면 {@code true}
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtProperties.getSECRET_KEY()).build().parseClaimsJws(token).getBody();
            return true;
        } catch (final JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰으로부터 파싱해서 유저 이메일 정보 획득
     *
     * @param token 검증할 토큰
     * @return 유효한 토큰이면 이메일 반환 {@code String}
     */
    public String getEmailFromJwtToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSECRET_KEY())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰을 헤더 응답에 포함
     *
     * @param response     응답 설정
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레스 토큰
     * @return 유효한 토큰이면 {@code true}
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response,
                                          String accessToken,
                                          String refreshToken
    ) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        log.info("Access Token 헤더 설정");
        response.setHeader(jwtProperties.getAccessHeader(), BEARER + accessToken);
    }

    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        log.info("Refresh Token 헤더 설정");
        response.setHeader(jwtProperties.getRefreshHeader(), BEARER + refreshToken);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getRefreshHeader()))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getAccessHeader()))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

}
