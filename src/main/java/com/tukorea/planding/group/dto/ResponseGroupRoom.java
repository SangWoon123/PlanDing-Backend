package com.tukorea.planding.group.dto;

import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.user.dto.UserInfo;
import lombok.Getter;

@Getter
public class ResponseGroupRoom {
    private Long id;
    private String title;
    private String code;
    private String ownerCode;

    public ResponseGroupRoom(Long id, String title, String code, String ownerCode) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.ownerCode = ownerCode;
    }

    public static ResponseGroupRoom from(GroupRoom groupRoom) {
        return new ResponseGroupRoom(groupRoom.getId(), groupRoom.getTitle(), groupRoom.getGroupCode(), groupRoom.getOwner());
    }
}
