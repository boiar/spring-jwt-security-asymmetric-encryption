package com.example.auth_security.auth.service.impl;

import com.example.auth_security.auth.exception.AuthErrorCode;
import com.example.auth_security.auth.exception.AuthException;
import com.example.auth_security.auth.mapper.AuthMapper;
import com.example.auth_security.auth.request.RegisterRequest;
import com.example.auth_security.auth.response.RefreshTokenResponse;
import com.example.auth_security.auth.response.RegisterResponse;
import com.example.auth_security.auth.service.interfaces.AuthenticationService;
import com.example.auth_security.auth.request.LoginRequest;
import com.example.auth_security.auth.request.RefreshRequest;
import com.example.auth_security.auth.response.LoginResponse;
import com.example.auth_security.common.mail.dto.RegisterMailDto;
import com.example.auth_security.common.mail.producer.MailQueueProducer;
import com.example.auth_security.common.mail.service.interfaces.MailService;
import com.example.auth_security.core.security.JwtService;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final MailQueueProducer mailQueueProducer;
    private  Authentication auth;

    @Override
    public LoginResponse login(LoginRequest request) {

        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException ex) {
            throw new AuthException(AuthErrorCode.USER_DISABLED);
        } catch (AuthenticationException ex) {
            throw new AuthException(AuthErrorCode.BAD_CREDENTIALS);
        }

        final User user = (User) auth.getPrincipal();

        if (user == null) {
            throw new AuthException(AuthErrorCode.BAD_CREDENTIALS);
        }

        final String token = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer";
        return authMapper.toLoginResponse(token, refreshToken, tokenType);
    }


    @Override
    @Transactional
    public RegisterResponse register(@Valid RegisterRequest req) {
        checkUserEmail(req.getEmail());
        checkUserPhoneNumber(req.getPhoneNumber());
        checkPasswords(req.getPassword(), req.getConfirmPassword());

        final User user = this.authMapper.toUserEntity(req);

        user.getActorData().setCreatedBy("user");
        /*TODO User Roles*/
        //user.setRoles();
        this.userRepo.save(user);

        // send welcome mail
        this.mailQueueProducer.enqueueRegisterMail(
                RegisterMailDto.builder()
                        .email(user.getEmail())
                        .username(user.getFullName())
                        .build()
        );

        final String token = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer";
        return authMapper.toRegisterResponse(token, refreshToken, tokenType, user);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshRequest req) {
        final String newAccessToken = this.jwtService.refreshAccessToken(req.getRefreshToken());
        final String tokenType = "Bearer";
        return authMapper.toRefreshTokenResponse(newAccessToken, tokenType);
    }


    private void checkUserEmail(final String email) {
        final boolean emailExists = this.userRepo.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
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

