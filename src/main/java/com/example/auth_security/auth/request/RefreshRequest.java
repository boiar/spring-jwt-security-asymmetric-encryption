package com.example.auth_security.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshRequest {


    @NotBlank(message = "err.refresh_token.not_blank")
    private String refreshToken;
}
