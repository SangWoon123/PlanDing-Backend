package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.dto.response.GroupFavoriteResponse;
import com.tukorea.planding.domain.group.entity.GroupFavorite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.repository.favorite.GroupFavoriteRepository;
import com.tukorea.planding.domain.group.repository.favorite.GroupFavoriteRepositoryCustom;
import com.tukorea.planding.domain.group.service.query.GroupQueryService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.domain.user.service.UserQueryService;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupFavoriteService {

    private final GroupFavoriteRepository groupFavoriteRepository;
    private final GroupFavoriteRepositoryCustom groupFavoriteRepositoryCustom;
    private final UserQueryService userQueryService;
    private final GroupQueryService groupQueryService;

    public GroupFavoriteResponse addFavorite(UserInfo userInfo, Long groupId) {

        boolean exists = groupFavoriteRepositoryCustom.existsByUserAndGroupRoom(userInfo.getUserCode(), groupId);
        if (exists) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_ADD);
        }

        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        GroupRoom groupRoom = groupQueryService.getGroupById(groupId);

        GroupFavorite groupFavorite = GroupFavorite.createGroupFavorite(user, groupRoom);
        GroupFavorite save = groupFavoriteRepository.save(groupFavorite);

        return GroupFavoriteResponse.from(save);
    }

    public void deleteFavorite(UserInfo userInfo, Long groupId) {
        try {
            groupFavoriteRepository.deleteByUserIdAndGroupRoomId(userInfo.getId(), groupId);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_DELETE);
        }
    }
}
