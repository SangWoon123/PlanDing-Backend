package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.tukorea.planding.domain.group.entity.QGroupRoom.groupRoom;


@Repository
@RequiredArgsConstructor
public class GroupRoomRepositoryCustomImpl implements GroupRoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<GroupRoom> findGroupRoomsByUserId(Long userId) {
        return queryFactory.select(groupRoom)
                .from(groupRoom)
                .innerJoin(groupRoom.groupMemberships)
                .on(groupRoom.groupMemberships.any().user.id.eq(userId))
                .fetch();
    }

    @Override
    public GroupRoom findByGroupCode(String groupCode) {
        return queryFactory.select(groupRoom)
                .from(groupRoom)
                .where(groupRoom.groupCode.eq(groupCode))
                .fetchOne();
    }
}
