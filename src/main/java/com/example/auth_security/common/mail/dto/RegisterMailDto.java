package com.example.auth_security.common.mail.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterMailDto {
    private String email;
    private String username;

}
