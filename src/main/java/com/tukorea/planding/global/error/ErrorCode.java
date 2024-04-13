package com.tukorea.planding.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * User Error
     */
    USER_NOT_FOUND("USER-001", "유저가 존재하지 않습니다.",HttpStatus.NOT_FOUND),

    /**
     * Schedule Error
     */
    SCHEDULE_NOT_FOUND("SCHEDULE-001", "해당 스케줄은 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_SCHEDULE("SCHEDULE-002", "본 사용자는 스케줄에 권한이 없습니다.", HttpStatus.UNAUTHORIZED),

    /**
     * Group Error
     */
    ACCESS_DENIED("GROUP-001", "본 사용자는 이 그룹에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    USER_NOT_INVITABLE("GROUP-002", "초대할 유저를 찾지 못하였습니다.", HttpStatus.NOT_FOUND),
    GROUP_ROOM_NOT_FOUND("GROUP-003", "그룹룸이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_GROUP_ROOM_INVITATION("GROUP-004", "그룹룸에 초대할 권한이 없습니다.", HttpStatus.UNAUTHORIZED);


    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
