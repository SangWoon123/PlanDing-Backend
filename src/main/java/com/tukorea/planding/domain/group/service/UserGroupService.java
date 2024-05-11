package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.repository.normal.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepository;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepositoryCustom;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final UserGroupRepositoryCustom userGroupRepositoryCustom;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final GroupRoomRepository groupRoomRepository;


    @Transactional
    public void updateConnectionStatus(String userCode, String groupCode, boolean isConnected) {

        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        if (!userGroupRepositoryCustom.existsByGroupRoomIdAndUserId(groupRoom.getId(), user.getId())) {
            throw new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND);
        }

        UserGroup test = userGroupRepositoryCustom.findUserByGroupId(user.getId(), groupRoom.getId());
        test.setConnected(isConnected);
    }

    public void save(UserGroup userGroup) {
        userGroupRepository.save(userGroup);
    }

}
