package com.tukorea.planding.domain.group.repository.invite;

import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    boolean existsByGroupRoomIdAndInvitedUserAndInviteStatus(Long groupId, User user, InviteStatus inviteStatus);

    Optional<GroupInvite> findByGroupInviteCode(String code);
}
