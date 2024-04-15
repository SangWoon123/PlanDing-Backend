package com.tukorea.planding.domain.invitation.repository;

import com.tukorea.planding.domain.invitation.entity.Invitation;
import com.tukorea.planding.domain.invitation.entity.InviteStatus;
import com.tukorea.planding.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    boolean existsByGroupRoomIdAndInvitedUserAndInviteStatus(Long groupId, User user, InviteStatus inviteStatus);
}
