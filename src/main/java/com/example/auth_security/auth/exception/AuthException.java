package com.example.auth_security.auth.exception;

import com.example.auth_security.core.exception.BaseException;

public class AuthException extends BaseException {
    public AuthException(AuthErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getStatus(), args);
    }
}
