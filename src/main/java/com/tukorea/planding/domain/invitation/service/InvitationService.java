package com.tukorea.planding.domain.invitation.service;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupRoomRepository;
import com.tukorea.planding.domain.group.repository.UserGroupMembershipRepository;
import com.tukorea.planding.domain.invitation.dto.InvitationRequest;
import com.tukorea.planding.domain.invitation.dto.InvitationResponse;
import com.tukorea.planding.domain.invitation.entity.Invitation;
import com.tukorea.planding.domain.invitation.entity.InviteStatus;
import com.tukorea.planding.domain.invitation.repository.InvitationRepository;
import com.tukorea.planding.domain.invitation.repository.InvitationRepositoryCustom;
import com.tukorea.planding.domain.notify.dto.NotificationScheduleRequest;
import com.tukorea.planding.domain.notify.entity.NotificationType;
import com.tukorea.planding.domain.notify.service.NotificationService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.repository.UserRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {

    private final UserRepository userRepository;
    private final GroupRoomRepository groupRoomRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationRepositoryCustom invitationRepositoryCustom;
    private final NotificationService notificationService;
    private final UserGroupMembershipRepository userGroupMembershipRepository;

    @Transactional
    public InvitationResponse inviteGroupRoom(UserInfo userInfo, InvitationRequest invitedUserInfo) {

        // 초대하는 유저가 존재하는지 체크하는 로직
        User invitingUser = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 초대하는 사용자와 초대 대상 사용자가 같은지 확인
        if (invitingUser.getUserCode().equals(invitedUserInfo.userCode())) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_YOURSELF);
        }

        GroupRoom groupRoom = groupRoomRepository.findByGroupCode(invitedUserInfo.inviteGroupCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_ROOM_NOT_FOUND));

        // 초대하는 유저가 방장인지 체크하는 로직
        validInvitePermission(groupRoom, invitingUser);

        User invitedUser = userRepository.findByUserCode(invitedUserInfo.userCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 초대한 유저가 이미 그룹에 속해 있는지 확인
        if (groupRoom.getGroupMemberships().contains(invitedUser)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INVITED);
        }

//        // 이미 보낸 초대인지 확인
//        if (invitationRepository.existsByGroupRoomIdAndInvitedUserAndInviteStatus(groupRoom.getId(), invitedUser, InviteStatus.PENDING)) {
//            throw new BusinessException(ErrorCode.INVITATION_ALREADY_SENT);
//        }


        Invitation invite = Invitation.builder()
                .groupRoom(groupRoom)
                .invitingUser(invitingUser)
                .invitedUser(invitedUser)
                .inviteStatus(InviteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();

        Invitation save = invitationRepository.save(invite);

        NotificationScheduleRequest request = NotificationScheduleRequest
                .builder()
                .type(NotificationType.INVITE)
                .groupName(groupRoom.getName())
                .message(groupRoom.getName() + "그룹으로 부터 초대되었습니다.")
                .receiverCode(invitedUser.getUserCode())
                .build();

        notificationService.send(request);

        return Invitation.toInviteResponse(save);
    }

    private void validInvitePermission(GroupRoom groupRoom, User invitingUser) {
        if (!groupRoom.getOwner().equals(invitingUser.getUserCode())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_GROUP_ROOM_INVITATION);
        }
    }

    public List<InvitationResponse> getInvitations(UserInfo userInfo) {
        User user = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Invitation> invitations = invitationRepositoryCustom.findByUser(user);

        return invitations.stream()
                .map(Invitation::toInviteResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvitationResponse acceptInvitation(UserInfo userInfo, String code) {
        User user = userRepository.findByUserCode(userInfo.getUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Invitation invitation = invitationRepository.findByInviteCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTEXIST_INVITE));

        invitation.accept();

        invitation.getGroupRoom().addUser(user);

        userGroupMembershipRepository.saveAll(invitation.getGroupRoom().getGroupMemberships());

        return Invitation.toInviteResponse(invitation);
    }
}
