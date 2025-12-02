package com.example.auth_security.auth.service.impl;

import com.example.auth_security.auth.exception.AuthErrorCode;
import com.example.auth_security.auth.exception.AuthException;
import com.example.auth_security.auth.service.interfaces.AuthenticationService;
import com.example.auth_security.auth.request.AuthenticationRequest;
import com.example.auth_security.auth.request.RefreshRequest;
import com.example.auth_security.auth.response.AuthenticationResponse;
import com.example.auth_security.common.request.RegistrationRequest;
import com.example.auth_security.security.JwtService;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.mapper.UserMapper;
import com.example.auth_security.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final UserMapper userMapper;


    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {

        final Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final User user = (User) auth.getPrincipal();

        if (user == null) {
            throw new AuthException(AuthErrorCode.BAD_CREDENTIALS);
        }

        final String token = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer";
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();

    }

    @Override
    public void register(RegistrationRequest request) {

    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        return null;
    }


    private void checkUserEmail(final String email) {
        final boolean emailExists = this.userRepo.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new AuthException(AuthErrorCode.BAD_CREDENTIALS);
        }
    }

    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }
    }


    private void checkUserPhoneNumber(final String phoneNumber) {
        final boolean phoneNumberExists = this.userRepo.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExists) {
            throw new AuthException(AuthErrorCode.PHONE_ALREADY_EXISTS);
        }
    }


}

