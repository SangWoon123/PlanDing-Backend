package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.entity.User;

import java.util.List;

public interface GroupRoomRepositoryCustom {
    List<GroupRoom> findGroupRoomsByUserId(Long userId);

    GroupRoom findByGroupId(Long groupId);

    List<User> getGroupUsers(Long groupId);
}
