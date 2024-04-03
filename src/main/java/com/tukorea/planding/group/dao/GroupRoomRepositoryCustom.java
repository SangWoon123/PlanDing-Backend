package com.tukorea.planding.group.dao;

import com.tukorea.planding.group.domain.GroupRoom;

import java.util.List;

public interface GroupRoomRepositoryCustom {
    List<GroupRoom> findGroupRoomsByUserId(Long userId);
}
