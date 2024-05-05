package com.tukorea.planding.domain.group.entity;

import com.tukorea.planding.domain.group.dto.InvitationResponse;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_id")
    private GroupRoom groupRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviting_user_id")
    private User invitingUser;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    private InviteStatus inviteStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    public Invitation(GroupRoom groupRoom, User invitedUser, User invitingUser, InviteStatus inviteStatus, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.groupRoom = groupRoom;
        this.invitedUser = invitedUser;
        this.invitingUser = invitingUser;
        this.inviteCode = generateInviteCode();
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

    private String generateInviteCode() {
        return "INV-" + LocalDateTime.now().toString();
    }

    public static InvitationResponse toInviteResponse(Invitation invitation) {
        return InvitationResponse.builder()
                .invitingUser(invitation.getInvitingUser().getUserCode())
                .groupName(invitation.getGroupRoom().getName())
                .inviteCode(invitation.getInviteCode())
                .inviteStatus(invitation.getInviteStatus())
                .build();
    }
}
