package com.example.auth_security.common.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;
    private final Object[] args;

    public BaseException(final String errorCode, final HttpStatus status, final Object... args){
        super();
        this.errorCode = errorCode;
        this.status = status;
        this.args = args;
    }


    public String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage(this.errorCode, args, this.errorCode, LocaleContextHolder.getLocale());
    }

}
