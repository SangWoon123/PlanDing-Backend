package com.tukorea.planding.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.tukorea.planding.domain.group.entity.QGroupFavorite.groupFavorite;

@Repository
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
    public Boolean existsByUserAndGroupRoom(String userCode) {
        Integer fetchOne = jpaQueryFactory.selectOne()
                .from(groupFavorite)
                .where(groupFavorite.user.userCode.eq(userCode))
                .fetchFirst();
        return fetchOne != null;
    }
}
