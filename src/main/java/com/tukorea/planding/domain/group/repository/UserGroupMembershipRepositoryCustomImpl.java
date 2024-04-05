package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.tukorea.planding.domain.group.entity.QUserGroupMembership.userGroupMembership;


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
