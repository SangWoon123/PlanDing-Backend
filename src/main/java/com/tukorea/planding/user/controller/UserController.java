package com.tukorea.planding.user.controller;

import com.tukorea.planding.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "User", description = "회원 API 문서")
@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Operation(summary = "유저 정보 가져오기")
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserInfo userInfo){
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
