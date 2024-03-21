package com.tukorea.planding.global.error;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected CommonResponse<?> handleIllegalArgumentException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "400");
        return CommonUtils.fail(errorResponse);
    }

    @ExceptionHandler(value = { UsernameNotFoundException.class })
    protected CommonResponse<?> handlerUsernameNotFoundException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "400");
        return CommonUtils.fail(errorResponse);
    }
}
