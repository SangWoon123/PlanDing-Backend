package com.tukorea.planding.domain.invitation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.invitation.entity.Invitation;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.domain.invitation.entity.QInvitation.invitation;

@Repository
@RequiredArgsConstructor
public class InvitationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<Invitation> findByUser(User user) {
        return queryFactory.selectFrom(invitation)
                .where(invitation.invitedUser.userCode.eq(user.getUserCode()))
                .fetch();
    }
}
