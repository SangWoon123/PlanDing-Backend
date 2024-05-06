package com.tukorea.planding.domain.group.repository;

public interface GroupFavoriteRepositoryCustom {
    Long countMyFavoriteGroup(String userCode);
    Boolean existsByUserAndGroupRoom(String userCode);
}
