package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.domain.group.entity.QInvitation.invitation;


@Repository
@RequiredArgsConstructor
public class GroupInviteRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<GroupInvite> findPendingInvitationsForUser(User user) {
        return queryFactory.selectFrom(invitation)
                .where(invitation.invitedUser.userCode.eq(user.getUserCode())
                        .and(invitation.inviteStatus.eq(InviteStatus.PENDING)))
                .fetch();
    }

    public Long countByInvitedUserCodeAndStatus(String userCode, InviteStatus inviteStatus) {
        return queryFactory.select(invitation.count())
                .from(invitation)
                .where(invitation.invitedUser.userCode.eq(userCode).and(
                        invitation.inviteStatus.eq(inviteStatus)
                ))
                .fetchOne();
    }
}
