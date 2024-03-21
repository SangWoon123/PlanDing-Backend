package com.tukorea.planding.global.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private String code;

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
