package com.tukorea.planding.global.jwt.token.service;

import com.tukorea.planding.global.jwt.token.dao.RefreshToken;
import com.tukorea.planding.global.jwt.token.repository.RefreshTokenRepository;
import com.tukorea.planding.user.dao.UserRepository;
import com.tukorea.planding.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void updateRefreshToken(String username, String refreshToken) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(NoSuchElementException::new);

        RefreshToken findRefreshToken = refreshTokenRepository.findByUser(user)
                .orElse(null);

        if (findRefreshToken == null) {
            RefreshToken updateRefreshToken = RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();
            refreshTokenRepository.save(updateRefreshToken);
            return;
        }

        findRefreshToken.update(refreshToken);
        log.info("User: {}의 리프레시 토큰 업데이트 완료", user);
    }

}
