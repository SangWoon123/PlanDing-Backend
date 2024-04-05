package com.tukorea.planding.global.config.security.jwt;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenHandler {

    private final JwtProperties jwtProperties;

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
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSECRET()) // HS512과 비교했을때 본 서비스는 암호화 난이도를 낮게 설정
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
            Jwts.parserBuilder().setSigningKey(jwtProperties.getSECRET()).build().parseClaimsJws(token).getBody();
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
                .setSigningKey(jwtProperties.getSECRET())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getRefreshHeader()))
                .filter(refreshToken -> refreshToken.startsWith(JwtConstant.BEARER.getValue()))
                .map(refreshToken -> refreshToken.replace(JwtConstant.BEARER.getValue(), ""));
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getAccessHeader()))
                .filter(accessToken -> accessToken.startsWith(JwtConstant.BEARER.getValue()))
                .map(accessToken -> accessToken.replace(JwtConstant.BEARER.getValue(), ""));
    }

}
