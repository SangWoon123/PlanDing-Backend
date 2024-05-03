package com.tukorea.planding.domain.group.dto;

import com.tukorea.planding.domain.group.entity.GroupRoom;
import lombok.Builder;
import lombok.Getter;

@Builder
public record GroupResponse(
        Long id,
        String name,
        String description,
        String code,
        String ownerCode
) {
    public static GroupResponse from(GroupRoom groupRoom) {
        return new GroupResponse(groupRoom.getId(), groupRoom.getName(), groupRoom.getDescription(), groupRoom.getGroupCode(), groupRoom.getOwner());
    }
}
