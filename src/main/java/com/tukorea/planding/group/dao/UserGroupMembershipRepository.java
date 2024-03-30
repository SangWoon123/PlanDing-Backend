package com.tukorea.planding.group.dao;

import com.tukorea.planding.group.domain.UserGroupMembership;
import com.tukorea.planding.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership,Long> {
}
