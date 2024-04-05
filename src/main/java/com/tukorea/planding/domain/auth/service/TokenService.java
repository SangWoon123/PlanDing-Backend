package com.tukorea.planding.domain.auth.service;

import com.tukorea.planding.domain.auth.dto.TokenResponse;
import com.tukorea.planding.domain.auth.repository.TokenInfoCacheRepository;
import com.tukorea.planding.global.jwt.token.service.JwtTokenHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {


    private final JwtTokenHandler jwtTokenHandler;
    private final TokenInfoCacheRepository tokenInfoCacheRepository;


    public TokenResponse createNewToken(final String email) {
        TokenResponse tokenResponse = createTokenResponse(email);
        tokenInfoCacheRepository.save(tokenResponse.refreshToken(), email);
        return tokenResponse;
    }

    public TokenResponse reIssueToken(final String refreshToken) {
        String email = jwtTokenHandler.getEmailFromJwtToken(refreshToken);
        String userEmail = tokenInfoCacheRepository.getUserInfo(refreshToken)
                .orElseThrow(IllegalAccessError::new);

        String newRefreshToken = jwtTokenHandler.generateRefreshToken(email);
        tokenInfoCacheRepository.rename(refreshToken, newRefreshToken);

        return createTokenResponse(userEmail);
    }

    private TokenResponse createTokenResponse(final String email) {
        return new TokenResponse(
                jwtTokenHandler.generateAccessToken(email),
                jwtTokenHandler.generateRefreshToken(email)
        );
    }
}
