package com.example.auth_security.auth.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum AuthErrorCode {

    BAD_CREDENTIALS("err.bad.credentials", UNAUTHORIZED),
    USER_DISABLED("err.user.disabled", UNAUTHORIZED),
    INTERNAL_EXCEPTION("err.internal.exception", INTERNAL_SERVER_ERROR),
    ERR_SENDING_ACTIVATION_EMAIL("err.sending.activation.email", INTERNAL_SERVER_ERROR),
    INVALID_CURRENT_PASSWORD("err.invalid.current.password", BAD_REQUEST),
    PASSWORD_MISMATCH("err.password.mismatch", BAD_REQUEST),
    PHONE_ALREADY_EXISTS("err.phone.already.exists", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("err.change.password.mismatch", BAD_REQUEST),
    USERNAME_NOT_FOUND("err.username.not.found", NOT_FOUND);


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
