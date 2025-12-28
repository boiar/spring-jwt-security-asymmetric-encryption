package com.example.auth_security.todo.exception;

import com.example.auth_security.category.exception.CategoryErrorCode;
import com.example.auth_security.core.exception.BaseException;

public class TodoException extends BaseException {
    public TodoException(TodoErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getStatus(), args);
    }
}
