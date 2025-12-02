package com.example.auth_security.user.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum UserErrorCode {

    EMAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", CONFLICT),
    PHONE_ALREADY_EXISTS("ERR_PHONE_EXISTS", CONFLICT),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", BAD_REQUEST),
    USER_NOT_FOUND("ERR_USER_NOT_FOUND", NOT_FOUND),
    ACCOUNT_ALREADY_DEACTIVATED("ERR_ACCOUNT_ALREADY_DEACTIVATED", BAD_REQUEST),
    ACCOUNT_ALREADY_ACTIVATED("ERR_ACCOUNT_ALREADY_ACTIVATED", BAD_REQUEST),
    CATEGORY_ALREADY_EXISTS_FOR_USER("ERR_CATEGORY_ALREADY_EXISTS_FOR_USER", CONFLICT),
    INVALID_CURRENT_PASSWORD("ERR_INVALID_CURRENT_PASSWORD", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("ERR_CHANGE_PASSWORD_MISMATCH",BAD_REQUEST);




    private final String code;
    private final HttpStatus status;

    UserErrorCode(String code, HttpStatus status){
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
