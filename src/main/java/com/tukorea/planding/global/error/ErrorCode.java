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
    USER_NOT_FOUND("USER-001", "유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

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
    UNAUTHORIZED_GROUP_ROOM_INVITATION("GROUP-004", "그룹룸에 초대할 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_INVITED("GROUP-005", "이미 그룹룸에 초대되었습니다.", HttpStatus.UNAUTHORIZED),
    FAVORITE_ALREADY_ADD("GROUP-006", "이미 즐겨찾기에 등록하였습니다..", HttpStatus.UNAUTHORIZED),

    /**
     * Invite Error
     */
    INVITATION_ALREADY_SENT("INVITATION-001", "이미 초대한 사용자입니다.", HttpStatus.CONFLICT),
    CANNOT_INVITE_YOURSELF("INVITATION-002", "자기 자신은 초대를 할 수 없습니다.", HttpStatus.CONFLICT),
    NOTEXIST_INVITE("INVITATION-004", "초대받은 적이 없습니다.", HttpStatus.NOT_FOUND),

    /**
     * Group-Schedule-ATTENDANCE Error
     */
    INVALID_ATTENDANCE_STATUS("ATTENDANCE-001", "올바르지 않은 상태값입니다.", HttpStatus.NOT_FOUND);


    private final String errorCode;
    private final String message;
    private final HttpStatus status;
    }
