package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.Invitation;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.domain.group.entity.QInvitation.invitation;


@Repository
@RequiredArgsConstructor
public class InvitationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<Invitation> findPendingInvitationsForUser(User user) {
        return queryFactory.selectFrom(invitation)
                .where(invitation.invitedUser.userCode.eq(user.getUserCode())
                        .and(invitation.inviteStatus.eq(InviteStatus.PENDING)))
                .fetch();
    }
}
