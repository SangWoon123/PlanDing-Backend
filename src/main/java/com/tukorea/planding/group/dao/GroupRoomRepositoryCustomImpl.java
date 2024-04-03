package com.tukorea.planding.group.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.group.domain.GroupRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.group.domain.QGroupRoom.groupRoom;

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
}
