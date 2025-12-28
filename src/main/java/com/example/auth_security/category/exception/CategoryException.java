package com.example.auth_security.category.exception;

import com.example.auth_security.core.exception.BaseException;

public class CategoryException extends BaseException {
    public CategoryException(CategoryErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getStatus(), args);
    }
}
