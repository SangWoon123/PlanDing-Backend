package com.tukorea.planding.global.jwt.token.repository;


import com.tukorea.planding.global.jwt.token.entity.RefreshToken;
import com.tukorea.planding.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByUser(User user);
}
