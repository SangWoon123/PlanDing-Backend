package com.tukorea.planding.global.error;

import com.tukorea.planding.common.CommonResponse;
import com.tukorea.planding.common.CommonUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(value = { BusinessException.class })
    protected CommonResponse<?> handleBusinessException(BusinessException ex) {
        final ErrorCode errorCode = ex.getErrorCode();
        final ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return CommonUtils.fail(errorResponse);
    }
}
