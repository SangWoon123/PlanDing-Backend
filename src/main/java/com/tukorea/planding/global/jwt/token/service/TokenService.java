package com.tukorea.planding.global.jwt.token.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class TokenService {

    private final String SECRET_KEY;
    private String accessHeader;
    private String refreshHeader;
    private long accessExpiration;
    private long refreshExpiration;
    private static final String BEARER = "Bearer ";
    // redis

    public TokenService(@Value("${jwt.secret}") String SECRET_KEY,
                        @Value("${jwt.access-expiration}") long accessExpiration,
                        @Value("${jwt.refresh-expiration}") long refreshExpiration,
                        @Value("${jwt.access-header}") String accessHeader,
                        @Value("${jwt.refresh-header}") String refreshHeader
                        ) {
        this.SECRET_KEY = SECRET_KEY;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.accessHeader = accessHeader;
        this.refreshHeader = refreshHeader;
    }

    public String generateToken(long expiration, String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // HS512과 비교했을때 본 서비스는 암호화 난이도를 낮게 설정
                .compact();
    }

    // JWT 토큰 생성
    public String generateAccessToken(String email) {
        return generateToken(accessExpiration, email);
    }

    public String generateRefreshToken(String email) {
        String refreshToken = generateToken(refreshExpiration, email);
        return refreshToken;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String resolveSubject(String token) {
        Claims claim = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claim.getSubject();
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response,
                                          String accessToken,
                                          String refreshToken
    ) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshCookie(response, refreshToken);
    }

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        log.info("Access Token 헤더 설정");
        response.setHeader(accessHeader, BEARER + accessToken);
    }

    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        log.info("Refresh Token 헤더 설정");
        response.setHeader(refreshHeader, BEARER + refreshToken);
    }

    public void setRefreshCookie(HttpServletResponse response,String refreshToken){
        Cookie cookie = new Cookie("Authorization", BEARER+refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshExpiration);
        cookie.setHttpOnly(true); // 자바스크립트에서 쿠키 접근 불가 설정
        response.addCookie(cookie);
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
