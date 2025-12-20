package com.example.auth_security.auth.handler;

import com.example.auth_security.core.handler.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.auth_security.auth.exception.AuthErrorCode.USER_DISABLED;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final MessageSource messageSource;


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleBaseException() {
        String message = USER_DISABLED.getMessage(messageSource);

        final ErrorResponse body = ErrorResponse.builder()
                .code(USER_DISABLED.getCode())
                .message(message)
                .build();

        return ResponseEntity.status(USER_DISABLED.getStatus()).body(body);
    }




}
