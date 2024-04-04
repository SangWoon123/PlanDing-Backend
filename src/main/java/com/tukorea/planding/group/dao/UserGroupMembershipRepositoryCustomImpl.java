package com.tukorea.planding.group.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.group.domain.QUserGroupMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.tukorea.planding.group.domain.QUserGroupMembership.userGroupMembership;

@Repository
@RequiredArgsConstructor
public class UserGroupMembershipRepositoryCustomImpl implements UserGroupMembershipRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public boolean existsByGroupRoomIdAndUserId(Long groupRoomId, Long userId) {
        return queryFactory.selectFrom(userGroupMembership)
                .where(userGroupMembership.groupRoom.id.eq(groupRoomId)
                        .and(userGroupMembership.user.id.eq(userId)))
                .fetchCount() > 0;
    }
}
