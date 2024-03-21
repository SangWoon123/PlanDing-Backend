package com.tukorea.planding.user.dao;

import com.tukorea.planding.user.domain.SocialType;
import com.tukorea.planding.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCode(String userCode);
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
