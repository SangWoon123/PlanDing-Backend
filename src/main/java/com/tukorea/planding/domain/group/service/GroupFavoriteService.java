package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.GroupFavoriteResponse;
import com.tukorea.planding.domain.group.entity.GroupFavorite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.GroupFavoriteRepository;
import com.tukorea.planding.domain.group.repository.GroupFavoriteRepositoryCustom;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupFavoriteService {

    private final GroupFavoriteRepository groupFavoriteRepository;
    private final GroupFavoriteRepositoryCustom groupFavoriteRepositoryCustom;
    private final UserQueryService userQueryService;
    private final GroupQueryService groupQueryService;

    public GroupFavoriteResponse addFavorite(UserInfo userInfo, String groupCode) {
        User user = userQueryService.getByUserInfo(userInfo.getUserCode());
        GroupRoom groupRoom = groupQueryService.getGroupByCode(groupCode);

        boolean exists = groupFavoriteRepositoryCustom.existsByUserAndGroupRoom(user.getUserCode());
        if (exists) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_ADD);
        }

        GroupFavorite groupFavorite = GroupFavorite.createGroupFavorite(user, groupRoom);
        GroupFavorite save = groupFavoriteRepository.save(groupFavorite);

        return GroupFavoriteResponse.from(save);
    }
}
