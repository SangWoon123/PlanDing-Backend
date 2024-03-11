package com.tukorea.planding.global.jwt.token.repository;


import com.tukorea.planding.global.jwt.token.dao.RefreshToken;
import com.tukorea.planding.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByUser(User user);
}
