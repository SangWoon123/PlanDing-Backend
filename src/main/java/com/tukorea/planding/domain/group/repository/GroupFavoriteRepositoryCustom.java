package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.GroupFavorite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.user.entity.User;

import java.util.List;

public interface GroupFavoriteRepositoryCustom {
    Long countMyFavoriteGroup(String userCode);
    Boolean existsByUserAndGroupRoom(String userCode);
    GroupFavorite findByUserAndGroupRoom(User user, GroupRoom groupRoom);
    List<GroupFavorite> findByUserFavorite(Long userId);
}
