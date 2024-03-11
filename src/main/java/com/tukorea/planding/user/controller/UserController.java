package com.tukorea.planding.user.controller;

import com.tukorea.planding.global.oauth.details.CustomUser;
import com.tukorea.planding.user.dto.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserInfo userInfo){
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
