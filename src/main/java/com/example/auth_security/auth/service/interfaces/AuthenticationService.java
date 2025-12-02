package com.example.auth_security.auth.service.interfaces;

import com.example.auth_security.auth.request.AuthenticationRequest;
import com.example.auth_security.auth.request.RefreshRequest;
import com.example.auth_security.auth.response.AuthenticationResponse;
import com.example.auth_security.common.request.RegistrationRequest;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);
    void register(RegistrationRequest request);
    AuthenticationResponse refreshToken(RefreshRequest request);

}
