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

    @NotBlank(message = "email.not.blank")
    @Email(message = "err.email.format")
    @Schema(example = "ali@mail.com")
    String email;

    @NotBlank(message = "password.not.blank")
    @Schema(example = "pAssword1!_")
    String password;
}
