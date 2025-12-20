package com.example.auth_security.core.handler;
import com.example.auth_security.core.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.example.auth_security.common.exception.CommonErrorCode.VALIDATION_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class BaseExceptionHandler {

    private final MessageSource messageSource;
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(final BaseException ex) {

        final ErrorResponse body = ErrorResponse.builder()
                                                .code(ex.getErrorCode())
                                                .message(ex.getLocalizedMessage(messageSource))
                                                .build();

        return ResponseEntity.status(ex.getStatus() != null ? ex.getStatus() : BAD_REQUEST).body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(final MethodArgumentNotValidException exp) {


        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();

        exp.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    final String fieldName = error.getField();
                    final String errorCode = error.getDefaultMessage();
                    final String localizedMsg =
                            messageSource.getMessage(
                                    errorCode,
                                    null,
                                    errorCode,
                                    LocaleContextHolder.getLocale()
                            );


                    errors.add(ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(localizedMsg)
                            .build());
                });


        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(VALIDATION_ERROR.getMessage(messageSource))
                .code(VALIDATION_ERROR.getCode())
                .validationErrors(errors)
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

}
