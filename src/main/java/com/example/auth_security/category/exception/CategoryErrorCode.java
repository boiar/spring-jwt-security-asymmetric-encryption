package com.example.auth_security.category.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum CategoryErrorCode {

    CATEGORY_ALREADY_EXISTS_FOR_USER("err.category.already_exists_for_user", CONFLICT),
    CATEGORY_NOT_EXISTS("err.category_not_exists", CONFLICT);


    private final String code;
    private final HttpStatus status;

    CategoryErrorCode(String code, HttpStatus status){
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
