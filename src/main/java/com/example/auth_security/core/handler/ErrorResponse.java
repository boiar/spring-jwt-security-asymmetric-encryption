package com.example.auth_security.core.handler;

import lombok.*;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ErrorResponse {
    String message;
    private String code;
    private List<ValidationError> validationErrors;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ValidationError {
        private String field;
        private String code;
        private String message;
    }

}
