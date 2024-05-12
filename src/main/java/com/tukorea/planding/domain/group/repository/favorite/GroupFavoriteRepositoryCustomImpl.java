package com.tukorea.planding.domain.group.repository.favorite;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.domain.group.entity.QGroupFavorite.groupFavorite;
import static com.tukorea.planding.domain.group.entity.QGroupRoom.groupRoom;

@RequiredArgsConstructor
public class GroupFavoriteRepositoryCustomImpl implements GroupFavoriteRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long countMyFavoriteGroup(String userCode) {
        return jpaQueryFactory.select(groupFavorite.count())
                .from(groupFavorite)
                .where(
                        groupFavorite.user.userCode.eq(userCode))
                .fetchOne();
    }

    @Override
    public Boolean existsByUserAndGroupRoom(String userCode, Long groupRoomId) {
        long existsCount = jpaQueryFactory
                .selectOne()
                .from(groupFavorite)
                .join(groupFavorite.groupRoom, groupRoom)
                .where(groupFavorite.user.userCode.eq(userCode)
                        .and(groupFavorite.groupRoom.id.eq(groupRoomId)))
                .fetchCount();
        return existsCount > 0;
    }


}
