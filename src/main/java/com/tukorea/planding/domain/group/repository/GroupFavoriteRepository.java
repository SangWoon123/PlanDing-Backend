package com.tukorea.planding.domain.group.repository;

import com.tukorea.planding.domain.group.entity.GroupFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupFavoriteRepository extends JpaRepository<GroupFavorite,Long> {
}
