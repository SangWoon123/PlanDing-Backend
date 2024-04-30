package com.tukorea.planding.domain.notify.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import com.tukorea.planding.domain.notify.dto.FcmMessageDto;
import com.tukorea.planding.domain.notify.dto.FcmToClientRequest;
import com.tukorea.planding.domain.notify.dto.NotifyGroupRequest;
import com.tukorea.planding.domain.notify.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    @Operation(description = "푸시 알림전송")
    public ResponseEntity<?> sendNotification(@RequestBody FcmToClientRequest fcmToClientRequest) {
        try {
            fcmService.pushAlarm(fcmToClientRequest);
            return ResponseEntity.ok().build();
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

