package com.tukorea.planding.domain.group.repository;

public interface UserGroupMembershipRepositoryCustom {
    boolean existsByGroupRoomIdAndUserId(Long groupRoomId, Long userId);
}
