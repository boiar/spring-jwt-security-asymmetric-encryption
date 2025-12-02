package com.example.auth_security.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {

    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "ERR_EMAIL_FORMAT")
    @Schema(example = "ali@mail.com")
    String email;

    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Schema(example = "pAssword1!_")
    String password;
}
