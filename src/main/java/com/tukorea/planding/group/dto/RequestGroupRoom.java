package com.tukorea.planding.group.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestGroupRoom {
    private String userEmail;
    private String userCode;
    private String inviteGroupCode;

    // 요청중 유저의 이메일 또는 유저코드 중 하나로 초대보낼때 사용
    public static RequestGroupRoom checking(RequestGroupRoom requestGroupRoom){
        if(requestGroupRoom.getUserCode()==null){
            return RequestGroupRoom.builder()
                    .inviteGroupCode(requestGroupRoom.getInviteGroupCode())
                    .userEmail(requestGroupRoom.getUserEmail())
                    .userCode(null)
                    .build();
        }
        return RequestGroupRoom.builder()
                .inviteGroupCode(requestGroupRoom.getInviteGroupCode())
                .userCode(requestGroupRoom.getUserCode())
                .userEmail(null)
                .build();
    }
}
