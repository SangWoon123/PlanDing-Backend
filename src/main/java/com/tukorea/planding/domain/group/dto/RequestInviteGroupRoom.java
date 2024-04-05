package com.tukorea.planding.domain.group.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestInviteGroupRoom {
    private String userEmail;
    private String userCode;
    private String inviteGroupCode;

    // 요청중 유저의 이메일 또는 유저코드 중 하나로 초대보낼때 사용
    public static RequestInviteGroupRoom checking(RequestInviteGroupRoom requestInviteGroupRoom){
        if(requestInviteGroupRoom.getUserCode()==null){
            return RequestInviteGroupRoom.builder()
                    .inviteGroupCode(requestInviteGroupRoom.getInviteGroupCode())
                    .userEmail(requestInviteGroupRoom.getUserEmail())
                    .userCode(null)
                    .build();
        }
        return RequestInviteGroupRoom.builder()
                .inviteGroupCode(requestInviteGroupRoom.getInviteGroupCode())
                .userCode(requestInviteGroupRoom.getUserCode())
                .userEmail(null)
                .build();
    }
}
