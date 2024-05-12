package com.tukorea.planding.domain.group.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.group.dto.response.GroupFavoriteResponse;
import com.tukorea.planding.domain.group.service.GroupFavoriteService;
import com.tukorea.planding.domain.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class GroupFavoriteController {

    private final GroupFavoriteService groupFavoriteService;

    @Operation(summary = "그룹 즐겨찾기 추가")
    @GetMapping("/{groupId}")
    public CommonResponse<GroupFavoriteResponse> addFavorite(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long groupId) {
        GroupFavoriteResponse response = groupFavoriteService.addFavorite(userInfo, groupId);
        return CommonUtils.success(response);
    }

    @Operation(summary = "그룹 즐겨찾기 해제")
    @DeleteMapping("/{groupId}")
    public CommonResponse<?> deleteFavorite(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long groupId) {
        groupFavoriteService.deleteFavorite(userInfo, groupId);
        return CommonUtils.success("즐겨찾기 해제 완료.");
    }

}
