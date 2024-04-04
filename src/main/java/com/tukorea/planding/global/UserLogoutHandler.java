package com.tukorea.planding.global;

import com.tukorea.planding.global.jwt.redis.RedisService;
import com.tukorea.planding.global.jwt.token.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserLogoutHandler implements LogoutHandler {

    private final RedisService redisService;
    private final JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtService.extractAccessToken(request).get();
        String email= jwtService.getEmailFromJwtToken(token);
        redisService.delete(email);
        log.info("Redis: 로그아웃 완료");
    }
}
