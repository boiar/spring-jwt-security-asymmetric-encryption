package com.example.auth_security.auth.controller.api.v1;

import com.example.auth_security.auth.request.LoginRequest;
import com.example.auth_security.auth.request.RefreshRequest;
import com.example.auth_security.auth.request.RegisterRequest;
import com.example.auth_security.auth.response.LoginResponse;
import com.example.auth_security.auth.response.RefreshTokenResponse;
import com.example.auth_security.auth.response.RegisterResponse;
import com.example.auth_security.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            @Valid
            @RequestBody
            final LoginRequest req){
        return ResponseEntity.ok(this.authService.login(req));
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid
            @RequestBody
            final RegisterRequest request
    ) {

        return ResponseEntity.ok(this.authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> generateAccessToken(
            @Valid
            @RequestBody
            final RefreshRequest request
    ) {

        return ResponseEntity.ok(this.authService.refreshToken(request));
    }




}
