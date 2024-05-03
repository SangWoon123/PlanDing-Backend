package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.GroupRoom;

import java.util.List;
import java.util.Optional;

public interface GroupRoomRepositoryCustom {
    List<GroupRoom> findGroupRoomsByUserId(Long userId);
    GroupRoom findByGroupCode(String groupCode);
}
