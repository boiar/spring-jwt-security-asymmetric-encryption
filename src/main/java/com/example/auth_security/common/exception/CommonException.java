package com.example.auth_security.common.exception;

import com.example.auth_security.core.exception.BaseException;

public class CommonException extends BaseException {
    public CommonException(CommonErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getStatus(), args);
    }
}
