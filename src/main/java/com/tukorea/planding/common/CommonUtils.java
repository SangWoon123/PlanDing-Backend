package com.tukorea.planding.common;

public class CommonUtils {
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null);
    }

    public static CommonResponse<?> fail(ErrorResponse error) {
        return new CommonResponse<>(false, null, error);
    }
}
