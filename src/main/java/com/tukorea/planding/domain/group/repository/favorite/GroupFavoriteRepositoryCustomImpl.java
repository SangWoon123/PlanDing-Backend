package com.tukorea.planding.domain.group.repository.favorite;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tukorea.planding.domain.group.entity.GroupFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tukorea.planding.domain.group.entity.QGroupFavorite.groupFavorite;

@Repository
@RequiredArgsConstructor
public class GroupFavoriteRepositoryCustomImpl implements GroupFavoriteRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GroupFavorite> findByUserFavorite(Long userId) {
        return null;
    }

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

    @Override
    public GroupFavorite findByUserIdAndGroupId(Long userId, Long groupId) {
        return jpaQueryFactory.selectFrom(groupFavorite)
                .where(groupFavorite.user.id.eq(userId).and(groupFavorite.groupRoom.id.eq(groupId)))
                .fetchOne();
    }


}
