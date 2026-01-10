package com.example.auth_security.user.response;

import com.example.auth_security.category.response.CategoryResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
