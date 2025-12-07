package com.example.auth_security.auth.controller.api.v1;

import com.example.auth_security.auth.request.AuthenticationRequest;
import com.example.auth_security.auth.response.AuthenticationResponse;
import com.example.auth_security.auth.service.interfaces.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login (
            @Valid
            @RequestBody
            final AuthenticationRequest req){
        return ResponseEntity.ok(this.authService.login(req));
    }

}
