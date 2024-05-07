package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.tukorea.planding.domain.group.entity.QGroupRoom.groupRoom;
import static com.tukorea.planding.domain.group.entity.QUserGroup.userGroup;
import static com.tukorea.planding.domain.user.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class GroupRoomRepositoryCustomImpl implements GroupRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupRoom> findGroupRoomsByUserId(Long userId) {
        return queryFactory.select(groupRoom)
                .from(groupRoom)
                .innerJoin(groupRoom.userGroups)
                .on(groupRoom.userGroups.any().user.id.eq(userId))
                .fetch();
    }

    @Override
    public GroupRoom findByGroupId(Long groupId) {
        return queryFactory.select(groupRoom)
                .from(groupRoom)
                .where(groupRoom.id.eq(groupId))
                .fetchOne();
    }

    @Override
    public List<User> getGroupUsers(Long groupId) {
        return queryFactory.select(user)
                .from(userGroup)
                .where(userGroup.groupRoom.id.eq(groupId))
                .fetch();
    }
}
