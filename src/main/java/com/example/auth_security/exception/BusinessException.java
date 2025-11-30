package com.example.auth_security.exception;

import ch.qos.logback.core.spi.ErrorCodes;

public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(final ErrorCode errorCode, final Object... args){
        super();
        this.errorCode = errorCode;
        this.args = args;
    }
}
