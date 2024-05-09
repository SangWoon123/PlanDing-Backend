package com.tukorea.planding.domain.user.dto;

import lombok.Builder;

@Builder
public record ProfileResponse(
        Long groupFavorite,
        Long groupRequest
) {
}
