package com.example.auth_security.common.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.*;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum CommonErrorCode {



    VALIDATION_ERROR("validation.error", BAD_REQUEST);

    private final String code;
    private final HttpStatus status;

    CommonErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(this.code, null, this.code, LocaleContextHolder.getLocale());
    }

    public String getMessage(MessageSource messageSource, Object[] args) {
        return messageSource.getMessage(this.code, args, this.code, LocaleContextHolder.getLocale());
    }
}
