package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.UserGroupMembership;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepository;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepositoryCustomImpl;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGroupMemberShipService {

    private final UserGroupMembershipRepositoryCustomImpl userGroupMembershipRepositoryCustom;
    private final UserRepository userRepository;
    private final GroupRoomRepository groupRoomRepository;

    @Transactional
    public void updateConnectionStatus(String userCode, String groupCode, boolean isConnected) {

        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        if (!userGroupMembershipRepositoryCustom.existsByGroupRoomIdAndUserId(groupRoom.getId(), user.getId())) {
            throw new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND);
        }

        UserGroupMembership test = userGroupMembershipRepositoryCustom.findUserByGroupId(user.getId(), groupRoom.getId());
        test.setConnected(isConnected);

    }
}
