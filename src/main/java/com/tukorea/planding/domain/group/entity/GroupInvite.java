package com.tukorea.planding.domain.group.entity;

import com.tukorea.planding.domain.group.dto.response.GroupInviteResponse;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_invite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_id")
    private GroupRoom groupRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @Column(name = "inviting_user_code", unique = true)
    private String invitingUserCode;

    @Column(name = "group_invite_code", unique = true)
    private String groupInviteCode;

    @Enumerated(EnumType.STRING)
    private InviteStatus inviteStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    public GroupInvite(GroupRoom groupRoom, User invitedUser, String invitingUserCode, InviteStatus inviteStatus, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.groupRoom = groupRoom;
        this.invitedUser = invitedUser;
        this.invitingUserCode = invitingUserCode;
        this.groupInviteCode = generateInviteCode();
        this.inviteStatus = inviteStatus;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }


    public void accept() {
        if (this.inviteStatus != InviteStatus.PENDING) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INVITED);
        }
        this.inviteStatus = InviteStatus.ACCEPTED;
    }

    public void decline() {
        if (this.inviteStatus != InviteStatus.PENDING) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INVITED);
        }
        this.inviteStatus = InviteStatus.DECLINED;
    }

    public void checkInvited(String userCode) throws BusinessException {
        if (!this.invitedUser.getUserCode().equals(userCode)) {
            throw new BusinessException(ErrorCode.NOTEXIST_INVITE);
        }
    }

    private String generateInviteCode() {
        return "INV"+UUID.randomUUID().toString();
    }

    public static GroupInviteResponse toInviteResponse(GroupInvite groupInvite) {
        return GroupInviteResponse.builder()
                .invitingUser(groupInvite.getInvitingUserCode())
                .groupName(groupInvite.getGroupRoom().getName())
                .inviteCode(groupInvite.getGroupInviteCode())
                .inviteStatus(groupInvite.getInviteStatus())
                .build();
    }
}
