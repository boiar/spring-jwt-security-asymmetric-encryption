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
public class LoginRequest {

    @NotBlank(message = "err.email.not_blank")
    @Email(message = "err.email.format")
    @Schema(example = "ali@mail.com")
    private String email;

    @NotBlank(message = "err.password.not_blank")
    @Schema(example = "pAssword1!_")
    private String password;
}
