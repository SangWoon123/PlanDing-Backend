package com.tukorea.planding.group.dao;

import com.tukorea.planding.group.domain.GroupRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRoomRepository extends JpaRepository<GroupRoom,Long> {

    Optional<GroupRoom> findByGroupCode(String groupCode);
}
