//package com.tukorea.planding.group.service;
//
//import com.tukorea.planding.group.dao.GroupRoomRepository;
//import com.tukorea.planding.group.domain.GroupRoom;
//import com.tukorea.planding.group.dto.ResponseGroupRoom;
//import com.tukorea.planding.user.dao.UserRepository;
//import com.tukorea.planding.user.domain.User;
//import com.tukorea.planding.user.dto.UserInfo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class GroupRoomService {
//
//    private final UserRepository userRepository;
//    private final GroupRoomRepository groupRoomRepository;
//    public ResponseGroupRoom createGroupRoom(UserInfo userInfo) {
//        User user = userRepository.findByEmail(userInfo.getEmail())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        GroupRoom newGroupRoom = GroupRoom.builder()
//                .owner(user.getCode())
//                .build();
//
//        GroupRoom savedGroupRoom = groupRoomRepository.save(newGroupRoom);
//
//
//        return ResponseGroupRoom.from(savedGroupRoom);
//    }
//}
