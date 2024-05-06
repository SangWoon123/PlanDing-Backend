package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.user.entity.User;

import java.util.List;

public interface UserGroupRepositoryCustom {
    boolean existsByGroupRoomIdAndUserId(Long groupRoomId, Long userId);
    List<User> findUserByIsConnectionFalse(Long groupRoomId);
    public UserGroup findUserByGroupId(Long userId, Long groupRoomId);
}
