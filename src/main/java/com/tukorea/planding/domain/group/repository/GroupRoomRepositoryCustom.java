package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.GroupRoom;

import java.util.List;

public interface GroupRoomRepositoryCustom {
    List<GroupRoom> findGroupRoomsByUserId(Long userId);
}
