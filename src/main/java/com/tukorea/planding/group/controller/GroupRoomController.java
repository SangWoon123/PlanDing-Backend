//package com.tukorea.planding.group.controller;
//
//import com.tukorea.planding.common.CommonResponse;
//import com.tukorea.planding.common.CommonUtils;
//import com.tukorea.planding.group.dto.ResponseGroupRoom;
//import com.tukorea.planding.group.service.GroupRoomService;
//import com.tukorea.planding.user.dto.UserInfo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/group")
//public class GroupRoomController {
//    private final GroupRoomService groupRoomService;
//    @PostMapping()
//    public CommonResponse<ResponseGroupRoom> createGroupRoom(@AuthenticationPrincipal UserInfo userInfo){
//        ResponseGroupRoom responseGroupRoom=groupRoomService.createGroupRoom(userInfo);
//        return CommonUtils.success(responseGroupRoom);
//    }
//}
