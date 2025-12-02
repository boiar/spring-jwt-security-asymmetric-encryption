package com.example.auth_security.auth.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum AuthErrorCode {

    BAD_CREDENTIALS("ERR_BAD_CREDENTIALS", UNAUTHORIZED),
    USER_DISABLED("ERR_USER_DISABLED", UNAUTHORIZED),
    INTERNAL_EXCEPTION("ERR_INTERNAL_EXCEPTION", INTERNAL_SERVER_ERROR),
    ERR_SENDING_ACTIVATION_EMAIL("ERR_SENDING_ACTIVATION_EMAIL", INTERNAL_SERVER_ERROR),
    INVALID_CURRENT_PASSWORD("ERR_INVALID_CURRENT_PASSWORD", BAD_REQUEST),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", BAD_REQUEST),
    PHONE_ALREADY_EXISTS("ERR_PHONE_ALREADY_EXISTS", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("ERR_CHANGE_PASSWORD_MISMATCH", BAD_REQUEST),
    USERNAME_NOT_FOUND("ERR_USERNAME_NOT_FOUND", NOT_FOUND);

    private final String code;
    private final HttpStatus status;

    AuthErrorCode(String code, HttpStatus status){
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
