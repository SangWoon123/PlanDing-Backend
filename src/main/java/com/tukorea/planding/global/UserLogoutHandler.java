package com.tukorea.planding.global;

import com.tukorea.planding.global.jwt.redis.RedisService;
import com.tukorea.planding.global.jwt.token.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserLogoutHandler implements LogoutHandler {

    private final RedisService redisService;
    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = tokenService.extractAccessToken(request).get();
        String email=tokenService.resolveSubject(token);
        redisService.delete(email);
        log.info("Redis: 로그아웃 완료");
    }
}
