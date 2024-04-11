package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.UserGroupMembership;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.tukorea.planding.domain.group.entity.QUserGroupMembership.userGroupMembership;
import static com.tukorea.planding.domain.user.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class UserGroupMembershipRepositoryCustomImpl implements UserGroupMembershipRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByGroupRoomIdAndUserId(Long groupRoomId, Long userId) {
        return queryFactory.selectFrom(userGroupMembership)
                .where(userGroupMembership.groupRoom.id.eq(groupRoomId)
                        .and(userGroupMembership.user.id.eq(userId)))
                .fetchCount() > 0;
    }

    @Override
    public List<User> findUserByIsConnectionFalse(Long groupRoomId) {
        return queryFactory.selectFrom(user)
                .join(user.groupMemberships, userGroupMembership)
                .where(userGroupMembership.groupRoom.id.eq(groupRoomId)
                        .and(userGroupMembership.isConnected.eq(false)))
                .fetch();
    }

    public UserGroupMembership findUserByGroupId(Long userId, Long groupRoomId) {
        return queryFactory.selectFrom(userGroupMembership)
                .where(userGroupMembership.groupRoom.id.eq(groupRoomId)
                        .and(userGroupMembership.user.id.eq(userId)))
                .fetchFirst();
    }
}
