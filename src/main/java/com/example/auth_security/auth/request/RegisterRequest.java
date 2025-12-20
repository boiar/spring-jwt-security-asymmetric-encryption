package com.example.auth_security.auth.request;

import com.example.auth_security.common.validation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "err.first_name.not_blank")
    @Size(
            min = 1,
            max = 50,
            message = "err.first_name.size"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "err.first_name.string"
    )
    @Schema(example = "Ali")
    private String firstName;

    @NotBlank(message = "err.last_name.not_blank")
    @Size(
            min = 1,
            max = 50,
            message = "err.last_name.size"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "err.last_name.string"
    )
    @Schema(example = "John")
    private String lastName;

    @NotBlank(message = "err.email.not_blank")
    @Email(message = "err.email.format")
    @NonDisposableEmail(message = "err.email.disposable")
    @Schema(example = "ali@gmail.com")
    private String email;

    @NotBlank(message = "err.phone.not_blank")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "err.phone.pattern"
    )
    @Schema(example = "+201207964550")
    private String phoneNumber;

    @NotBlank(message = "err.password.not_blank")
    @Size(min = 8,
            max = 72,
            message = "err.password.size"
    )
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "err.password.weak"
    )
    @Schema(example = "pAssw0rd1!_")
    private String password;

    @NotBlank(message = "err.confirm_password.not_blank")
    @Schema(example = "pAssword1!_")
    private String confirmPassword;

    @NotNull(message = "err.date_of_birth.not_null")
    @Schema(example = "1998-05-21")
    private LocalDate dateOfBirth;
}

