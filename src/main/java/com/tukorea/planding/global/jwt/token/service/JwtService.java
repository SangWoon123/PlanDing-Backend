package com.tukorea.planding.global.jwt.token.service;


import com.tukorea.planding.global.jwt.redis.RedisService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JwtService {

    private final String SECRET_KEY;
    private String accessHeader;
    private String refreshHeader;
    private long accessExpiration;
    private long refreshExpiration;
    private static final String BEARER = "Bearer ";
    // redis
    private RedisService redisService;

    public JwtService(@Value("${jwt.secret}") String SECRET_KEY,
                      @Value("${jwt.access-expiration}") long accessExpiration,
                      @Value("${jwt.refresh-expiration}") long refreshExpiration,
                      @Value("${jwt.access-header}") String accessHeader,
                      @Value("${jwt.refresh-header}") String refreshHeader,
                      RedisService redisService
                        ) {
        this.SECRET_KEY = SECRET_KEY;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.accessHeader = accessHeader;
        this.refreshHeader = refreshHeader;
        this.redisService=redisService;
    }

    public String generateToken(long expiration, String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // HS512과 비교했을때 본 서비스는 암호화 난이도를 낮게 설정
                .compact();
    }

    public String generateAccessToken(String email) {
        return generateToken(accessExpiration, email);
    }

    public String generateRefreshToken(String email) {
        String refreshToken = generateToken(refreshExpiration, email);
        redisService.setValue(email,refreshToken,Duration.ofMillis(refreshExpiration));
        return refreshToken;
    }

    /**
     * 토큰을 파싱해서 올바른 토큰인지 확인
     *
     * @param token 검증할 토큰
     * @return 유효한 토큰이면 {@code true}
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            return true;
        }catch (final JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰을 헤더 응답에 포함
     *
     * @param response 응답 설정
     * @param accessToken 액세스 토큰
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
        response.setHeader(accessHeader, BEARER + accessToken);
    }

    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        log.info("Refresh Token 헤더 설정");
        response.setHeader(refreshHeader, BEARER + refreshToken);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

}
