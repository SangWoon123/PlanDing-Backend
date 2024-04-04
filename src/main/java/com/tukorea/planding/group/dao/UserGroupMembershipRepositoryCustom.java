package com.tukorea.planding.group.dao;

public interface UserGroupMembershipRepositoryCustom {
    boolean existsByGroupRoomIdAndUserId(Long groupRoomId, Long userId);
}
