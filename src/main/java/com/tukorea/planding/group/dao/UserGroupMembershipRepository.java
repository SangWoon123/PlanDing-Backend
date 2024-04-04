package com.tukorea.planding.group.dao;

import com.tukorea.planding.group.domain.UserGroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership,Long> {
}
