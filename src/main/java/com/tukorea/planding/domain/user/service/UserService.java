package com.tukorea.planding.domain.user.service;

import com.tukorea.planding.domain.group.entity.GroupFavorite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.schedule.service.ScheduleQueryService;
import com.tukorea.planding.domain.user.dto.AndroidLoginRequest;
import com.tukorea.planding.domain.user.dto.ProfileResponse;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.entity.SocialType;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.global.oauth.details.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserQueryService userQueryService;
    private final ScheduleQueryService scheduleQueryService;

    @Transactional(readOnly = true)
    public List<GroupRoom> findFavoriteGroupsByUserId(UserInfo userInfo) {
        return userQueryService.getUserByUserCode(userInfo.getUserCode())
                .getGroupFavorites()
                .stream()
                .map(GroupFavorite::getGroupRoom)
                .collect(Collectors.toList());
    }

    public User createUserFromRequest(AndroidLoginRequest androidLoginRequest) {
        User user = User.builder()
                .socialId(androidLoginRequest.socialId())
                .socialType(SocialType.KAKAO)
                .username(androidLoginRequest.profileNickname())
                .email(androidLoginRequest.accountEmail())
                .profileImage(androidLoginRequest.profileImage())
                .userCode(User.createCode())
                .role(Role.USER)
                .build();

        return userQueryService.save(user);
    }

    //TODO 즐겨찾는 그룹, 그룹 요청
    // N+1문제는 발생하지 않지만 user조회쿼리 3개나감
    public ProfileResponse getProfile(UserInfo userInfo) {
        User userProfile = userQueryService.getUserProfile(userInfo.getId());
        return ProfileResponse.builder()
                .userCode(userProfile.getUserCode())
                .email(userProfile.getEmail())
                .username(userProfile.getUsername())
                .profileImage(userProfile.getProfileImage())
                .groupRequest((long) userProfile.getGroupInvites().size())
                .groupFavorite((long) userProfile.getGroupFavorites().size())
                .role(Role.USER)
                .build();
    }
}
