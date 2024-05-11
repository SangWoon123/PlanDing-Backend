package com.tukorea.planding.domain.group.service.query;

import com.tukorea.planding.domain.group.repository.favorite.GroupFavoriteRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupFavoriteQueryService {

    private final GroupFavoriteRepositoryCustom groupFavoriteRepositoryCustom;

    public Long countMyFavoriteGroup(String userCode) {
        return groupFavoriteRepositoryCustom.countMyFavoriteGroup(userCode);
    }
}
