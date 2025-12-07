package com.example.auth_security.common.request;

import com.example.auth_security.common.validation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    @NotBlank(message = "{REGISTRATION_FIRST_NAME_BLANK}")
    @Size(
        min = 1,
        max = 50,
        message = "{REGISTRATION_FIRST_NAME_SIZE}"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "{REGISTRATION_FIRST_NAME_PATTERN}"
    )
    @Schema(example = "Ali")
    String firstName;


    @NotBlank(message = "{REGISTRATION_LAST_NAME_BLANK}")
    @Size(
            min = 1,
            max = 50,
            message = "{REGISTRATION_LAST_NAME_SIZE}"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "{REGISTRATION_LAST_NAME_PATTERN}"
    )
    @Schema(example = "weal")
    String lastName;

    @NotBlank(message = "{REGISTRATION_EMAIL_BLANK}")
    @Email(message = "{REGISTRATION_EMAIL_FORMAT}")
    @NonDisposableEmail(message = "VALIDATION_REGISTRATION_EMAIL_DISPOSABLE")
    @Schema(example = "ali@gmail.com")
    String email;

    @NotBlank(message = "{REGISTRATION_PHONE_BLANK}")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "{REGISTRATION_PHONE_FORMAT}"
    )
    @Schema(example = "+201207964550")
    String phoneNumber;


    @NotBlank(message = "{REGISTRATION_PASSWORD_BLANK}")
    @Size(min = 8,
            max = 72,
            message = "{REGISTRATION_PASSWORD_SIZE}"
    )
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "{REGISTRATION_PASSWORD_WEAK}"
    )
    @Schema(example = "pAssw0rd1!_")
    String password;

    @NotBlank(message = "{REGISTRATION_CONFIRM_PASSWORD_BLANK}")
    @Size(
            min = 8,
            max = 72,
            message = "{REGISTRATION_CONFIRM_PASSWORD_SIZE}"
    )
    @Schema(example = "pAssword1!_")
    String confirmPassword;





}
