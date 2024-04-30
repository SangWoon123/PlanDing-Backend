package com.tukorea.planding.domain.notify.dto;

import com.tukorea.planding.domain.notify.entity.NotificationType;
import lombok.Getter;

public record FcmToClientRequest(
        NotificationType type,
        String groupName,
        String message,
        String url
) {

}
