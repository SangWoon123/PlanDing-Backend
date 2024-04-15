package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.GroupCreateRequest;
import com.tukorea.planding.domain.group.dto.GroupResponse;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.GroupRoomRepositoryCustomImpl;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupRoomService {

    private final UserRepository userRepository;
    private final UserGroupMembershipRepository userGroupMembershipRepository;
    private final GroupRoomRepository groupRoomRepository;
    private final GroupRoomRepositoryCustomImpl groupRoomRepositoryCustom;

    @Transactional
    public GroupResponse createGroupRoom(UserInfo userInfo, GroupCreateRequest createGroupRoom) {
        User user = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupRoom newGroupRoom = GroupRoom.builder()
                .name(createGroupRoom.name())
                .owner(user.getUserCode())
                .build();

        newGroupRoom.addUser(user);
        GroupRoom savedGroupRoom = groupRoomRepository.save(newGroupRoom);

        // 중간테이블에 유저, 그룹 정보 저장
        userGroupMembershipRepository.saveAll(newGroupRoom.getGroupMemberships());

        return GroupResponse.from(savedGroupRoom);
    }

    // 유저가 속한 그룹룸 가져오기
    public List<GroupResponse> getAllGroupRoomByUser(UserInfo userInfo) {
        User user = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<GroupRoom> groupRooms = groupRoomRepositoryCustom.findGroupRoomsByUserId(user.getId());

        return groupRooms.stream()
                .map(GroupResponse::from)
                .collect(Collectors.toList());
    }
}
