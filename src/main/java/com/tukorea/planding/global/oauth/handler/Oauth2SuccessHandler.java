package com.tukorea.planding.global.oauth.handler;

import com.tukorea.planding.global.jwt.token.service.JwtUtil;
import com.tukorea.planding.global.jwt.token.service.RefreshTokenService;
import com.tukorea.planding.global.jwt.token.service.JwtTokenHandler;
import com.tukorea.planding.global.oauth.service.CustomOAuth2User;
import com.tukorea.planding.user.dao.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenHandler jwtTokenHandler;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("로그인성공");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();


        String accessToken = jwtTokenHandler.generateAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtTokenHandler.generateRefreshToken(oAuth2User.getEmail());


        log.info("token생성 ={}", accessToken);
        log.info("refresh생성 ={}", refreshToken);

        jwtUtil.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        refreshTokenService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);


        String url=makeRedirectUrl(accessToken,refreshToken);
        getRedirectStrategy().sendRedirect(request,response,url);
    }

    private String makeRedirectUrl(String accessToken,String refreshToken) {
        return UriComponentsBuilder.fromUriString("http://localhost:5173/login")
                .queryParam("accessToken",accessToken)
                .queryParam("refreshToken",refreshToken)
                .build().toUriString();
    }
}
