package com.tukorea.planding.group.dao;

import com.tukorea.planding.group.domain.GroupRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRoomRepository extends JpaRepository<GroupRoom,Long> {
    @Query("SELECT g.groupRoom FROM UserGroupMembership g WHERE g.user.id = :userId")
    List<GroupRoom> findGroupRoomsByUserId(Long userId);
    Optional<GroupRoom> findByGroupCode(String groupCode);
}
