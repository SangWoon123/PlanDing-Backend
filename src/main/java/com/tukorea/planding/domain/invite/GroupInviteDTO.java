package com.tukorea.planding.domain.invite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class GroupInviteDTO {
    private String inviteCode;
    private Long groupRoomId;
    private String invitedUserCode;
    private Long invitingUserId;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
