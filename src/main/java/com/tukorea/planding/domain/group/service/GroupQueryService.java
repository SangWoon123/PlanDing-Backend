package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.GroupRoomRepositoryCustom;
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

    public List<GroupRoom> findGroupsByUser(User user){
        return groupRoomRepositoryCustom.findGroupRoomsByUserId(user.getId());
    }

    public GroupRoom getGroupByCode(String groupCode) {
        return groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));
    }

}
