package com.tukorea.planding.domain.group.service.query;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.normal.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.normal.GroupRoomRepositoryCustom;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupQueryService {

    private final GroupRoomRepository groupRoomRepository;
    private final GroupRoomRepositoryCustom groupRoomRepositoryCustom;


    public GroupRoom createGroup(GroupRoom groupRoom) {
        return groupRoomRepository.save(groupRoom);
    }

    public List<GroupRoom> findGroupsByUserId(Long userId) {
        return groupRoomRepositoryCustom.findGroupRoomsByUserId(userId);
    }

    public GroupRoom getGroupById(Long groupId) {
        return groupRoomRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));
    }

    public void delete(GroupRoom groupRoom) {
        groupRoomRepository.delete(groupRoom);
    }

    public List<User> getGroupUsers(Long groupId) {
        return groupRoomRepositoryCustom.getGroupUsers(groupId);
    }

}
