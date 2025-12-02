package com.example.auth_security.user.exception;

import com.example.auth_security.common.exception.BaseException;

public class UserException extends BaseException {
    public UserException(UserErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getStatus(), args);
    }
}
