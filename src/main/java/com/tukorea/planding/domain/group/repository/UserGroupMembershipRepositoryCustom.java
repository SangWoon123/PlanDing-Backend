package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.user.entity.User;

import java.util.List;

public interface UserGroupMembershipRepositoryCustom {
    boolean existsByGroupRoomIdAndUserId(Long groupRoomId, Long userId);
    List<User> findUserByIsConnectionFalse(Long groupRoomId);
}
