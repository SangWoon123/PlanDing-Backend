package com.tukorea.planding.domain.user.controller;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.user.dto.AndroidLoginRequest;
import com.tukorea.planding.domain.user.dto.AndroidLoginResponse;
import com.tukorea.planding.domain.user.dto.UserInfo;
import com.tukorea.planding.domain.user.service.AndroidLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원 API 문서")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final AndroidLoginService androidLoginService;

    @Operation(summary = "유저 정보 가져오기")
    @GetMapping("/userInfo")
    public CommonResponse<UserInfo> getUserInfo(@AuthenticationPrincipal UserInfo userInfo) {
        return CommonUtils.success(userInfo);
    }

    @Operation(summary = "안드로이드 로그인")
    @PostMapping("/login/android/kakao")
    public CommonResponse<AndroidLoginResponse> signupApp(@RequestBody AndroidLoginRequest androidLoginRequest) {
        AndroidLoginResponse response = androidLoginService.signupApp(androidLoginRequest);
        return CommonUtils.success(response);
    }
}
