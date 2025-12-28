package com.example.auth_security.auth.mapper;

import com.example.auth_security.auth.request.RegisterRequest;
import com.example.auth_security.auth.response.LoginResponse;
import com.example.auth_security.auth.response.RefreshTokenResponse;
import com.example.auth_security.auth.response.RegisterResponse;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;
    public User toUserEntity(final RegisterRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .enabled(true)
                .locked(false)
                .credentialsExpired(false)
                .emailVerified(false)
                .phoneVerified(false)
                .timingData(new EntityAuditTimingData())
                .actorData(new EntityAuditActorData())
                .build();
    }

    public LoginResponse toLoginResponse(
            final String token,
            final String refreshToken,
            final String tokenType
    ) {
        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }


    public RefreshTokenResponse toRefreshTokenResponse(
            final String token,
            final String tokenType
    ) {
        return RefreshTokenResponse.builder()
                .accessToken(token)
                .tokenType(tokenType)
                .build();
    }


    public RegisterResponse toRegisterResponse(
            final String token,
            final String refreshToken,
            final String tokenType,
            final User user
    ) {
        return RegisterResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }


}
