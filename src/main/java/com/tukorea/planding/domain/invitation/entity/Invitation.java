package com.tukorea.planding.domain.invitation.entity;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        this.inviteStatus = inviteStatus;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
}
