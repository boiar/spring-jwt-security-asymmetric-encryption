package com.example.auth_security.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", CONFLICT),
    PHONE_ALREADY_EXISTS("ERR_PHONE_EXISTS", CONFLICT),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("ERR_CHANGE_PASSWORD_MISMATCH", BAD_REQUEST),
    ERR_SENDING_ACTIVATION_EMAIL("ERR_SENDING_ACTIVATION_EMAIL", INTERNAL_SERVER_ERROR),
    ERR_USER_DISABLED("ERR_USER_DISABLED", UNAUTHORIZED),
    INVALID_CURRENT_PASSWORD("ERR_INVALID_CURRENT_PASSWORD", BAD_REQUEST),
    USER_NOT_FOUND("ERR_USER_NOT_FOUND", NOT_FOUND),
    ACCOUNT_ALREADY_DEACTIVATED("ERR_ACCOUNT_ALREADY_DEACTIVATED", BAD_REQUEST),
    BAD_CREDENTIALS("ERR_BAD_CREDENTIALS", UNAUTHORIZED),
    INTERNAL_EXCEPTION("ERR_INTERNAL_EXCEPTION", INTERNAL_SERVER_ERROR),
    USERNAME_NOT_FOUND("ERR_USERNAME_NOT_FOUND", NOT_FOUND),
    CATEGORY_ALREADY_EXISTS_FOR_USER("ERR_CATEGORY_ALREADY_EXISTS_FOR_USER", CONFLICT);



    private final String code;
    private final HttpStatus status;

    ErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }


    /**
     * Get localized message from MessageSource.
     * @param messageSource Spring's MessageSource bean
     * @return Localized message
     */
    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(this.code, null, this.code, LocaleContextHolder.getLocale());
    }

    /**
     * Get localized message with arguments.
     * @param messageSource Spring's MessageSource bean
     * @param args Arguments for placeholders in message
     * @return Localized message
     */
    public String getMessage(MessageSource messageSource, Object[] args) {
        return messageSource.getMessage(this.code, args, this.code, LocaleContextHolder.getLocale());
    }

}
