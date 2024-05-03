package com.tukorea.planding.domain.group.dto;

public record GroupUpdateRequest(
        String name,
        String description,
        String groupCode
) {
}
