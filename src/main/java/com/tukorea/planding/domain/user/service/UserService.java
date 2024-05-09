package com.tukorea.planding.domain.user.service;

import com.tukorea.planding.domain.group.entity.GroupFavorite;
import com.tukorea.planding.domain.group.entity.GroupRoom;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.group.service.query.GroupFavoriteQueryService;
import com.tukorea.planding.domain.group.service.query.GroupInviteQueryService;
import com.tukorea.planding.domain.schedule.common.dto.ScheduleResponse;
import com.tukorea.planding.domain.schedule.entity.Schedule;
import com.tukorea.planding.domain.schedule.common.service.ScheduleQueryService;
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
    private final GroupInviteQueryService groupInviteQueryService;
    private final GroupFavoriteQueryService groupFavoriteQueryService;

    @Transactional(readOnly = true)
    public List<GroupRoom> findFavoriteGroupsByUserId(UserInfo userInfo) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        // 즐겨찾기된 그룹 목록을 반환
        return user.getGroupFavorites().stream()
                .map(GroupFavorite::getGroupRoom) // GroupFavorite 엔티티에서 GroupRoom을 가져오는 메서드 가정
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
    public ProfileResponse getProfile(UserInfo userInfo) {
        Long groupInvite = groupInviteQueryService.countInvitation(userInfo.getUserCode(), InviteStatus.PENDING);
        Long groupFavorite = groupFavoriteQueryService.countMyFavoriteGroup(userInfo.getUserCode());
        return ProfileResponse.builder()
                .groupRequest(groupInvite)
                .groupFavorite(groupFavorite)
                .build();
    }

    public List<ScheduleResponse> showTodaySchedule(UserInfo userInfo) {
        User user = userQueryService.getUserByUserCode(userInfo.getUserCode());
        List<Schedule> schedules = scheduleQueryService.showTodaySchedule(user.getId());
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }
}
