package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.UserGroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership,Long> {
}
