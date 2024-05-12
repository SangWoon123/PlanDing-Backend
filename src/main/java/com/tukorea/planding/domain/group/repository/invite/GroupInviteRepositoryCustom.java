package com.tukorea.planding.domain.group.repository.invite;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.domain.group.entity.QGroupInvite.groupInvite;
import static com.tukorea.planding.domain.group.entity.QGroupRoom.groupRoom;
import static com.tukorea.planding.domain.user.entity.QUser.user;


public interface GroupInviteRepositoryCustom {
    List<GroupInvite> findPendingInvitationsForUser(Long userId);
    Long countByInvitedUserCodeAndStatus(String userCode, InviteStatus inviteStatus);
}
