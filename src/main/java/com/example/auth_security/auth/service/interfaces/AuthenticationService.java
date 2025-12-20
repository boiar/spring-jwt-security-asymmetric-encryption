package com.example.auth_security.auth.service.interfaces;

import com.example.auth_security.auth.request.LoginRequest;
import com.example.auth_security.auth.request.RefreshRequest;
import com.example.auth_security.auth.request.RegisterRequest;
import com.example.auth_security.auth.response.LoginResponse;
import com.example.auth_security.auth.response.RefreshTokenResponse;
import com.example.auth_security.auth.response.RegisterResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);


    @Transactional
    RegisterResponse register(@Valid RegisterRequest req);

    RefreshTokenResponse refreshToken(RefreshRequest request);

}
