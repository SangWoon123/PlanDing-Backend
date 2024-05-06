package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepository;
import com.tukorea.planding.domain.group.dto.GroupInviteRequest;
import com.tukorea.planding.domain.group.dto.GroupInviteResponse;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.group.repository.GroupInviteRepository;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupInviteService {

    private final UserQueryService userQueryService;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupQueryService groupQueryService;
    private final GroupInviteQueryService groupInviteQueryService;


    private final NotificationService notificationService;
    private final UserGroupMembershipRepository userGroupMembershipRepository;


    @Transactional
    public GroupInviteResponse inviteGroupRoom(UserInfo userInfo, GroupInviteRequest invitedUserInfo) {

        // 초대하는 유저가 존재하는지 체크하는 로직
        User invitingUser = userQueryService.getByUserInfo(userInfo.getUserCode());

        // 초대하는 사용자와 초대 대상 사용자가 같은지 확인
        if (invitingUser.getUserCode().equals(invitedUserInfo.userCode())) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_YOURSELF);
        }

        GroupRoom groupRoom = groupQueryService.getGroupByCode(invitedUserInfo.inviteGroupCode());
        log.info("[그룹 초대] {} : {}", groupRoom.getName(), invitingUser.getUsername());

        // 초대하는 유저가 방장인지 체크하는 로직
        validInvitePermission(groupRoom, invitingUser);

        User invitedUser = userQueryService.getByUserInfo(invitedUserInfo.userCode());
        checkUserAlreadyOrInvited(groupRoom, invitedUser);

        GroupInvite invite = createAndSaveInvitation(groupRoom, invitingUser, invitedUser);

        sendGroupInviteNotification(invitedUser, groupRoom);

        return GroupInvite.toInviteResponse(invite);
    }

    public List<GroupInviteResponse> getInvitations(UserInfo userInfo) {
        User user = userQueryService.getByUserInfo(userInfo.getUserCode());

        List<GroupInvite> groupInvites = groupInviteQueryService.getPendingInvitationForUser(user);

        return groupInvites.stream()
                .map(GroupInvite::toInviteResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupInviteResponse acceptInvitation(UserInfo userInfo, String code) {
        User user = userQueryService.getByUserInfo(userInfo.getUserCode());

        GroupInvite groupInvite = groupInviteQueryService.getInvitationByInviteCode(code);
        groupInvite.accept();

        groupInvite.getGroupRoom().addUser(user);

        userGroupMembershipRepository.saveAll(groupInvite.getGroupRoom().getGroupMemberships());

        return GroupInvite.toInviteResponse(groupInvite);
    }

    private void sendGroupInviteNotification(User invitedUser, GroupRoom groupRoom) {
        NotificationScheduleRequest request = NotificationScheduleRequest
                .builder()
                .type(NotificationType.INVITE)
                .groupName(groupRoom.getName())
                .message(groupRoom.getName() + "그룹으로 부터 초대되었습니다.")
                .receiverCode(invitedUser.getUserCode())
                .build();

        notificationService.send(request);
    }

    private GroupInvite createAndSaveInvitation(GroupRoom groupRoom, User invitingUser, User invitedUser) {
        GroupInvite invite = GroupInvite.builder()
                .groupRoom(groupRoom)
                .invitingUser(invitingUser)
                .invitedUser(invitedUser)
                .inviteStatus(InviteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        return groupInviteRepository.save(invite);
    }

    private void validInvitePermission(GroupRoom groupRoom, User invitingUser) {
        if (!groupRoom.getOwner().equals(invitingUser.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_GROUP_ROOM_INVITATION);
        }
    }

    private void checkUserAlreadyOrInvited(GroupRoom groupRoom, User invitedUser) {
        // 초대한 유저가 이미 그룹에 속해 있는지 확인
        if (groupRoom.getGroupMemberships().contains(invitedUser)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INVITED);
        }
        // 이미 보낸 초대인지 확인
        if (groupInviteRepository.existsByGroupRoomIdAndInvitedUserAndInviteStatus(groupRoom.getId(), invitedUser, InviteStatus.PENDING)) {
            throw new BusinessException(ErrorCode.INVITATION_ALREADY_SENT);
        }
    }


}
