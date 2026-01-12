package com.example.auth_security.service.auth;

import com.example.auth_security.auth.exception.AuthErrorCode;
import com.example.auth_security.auth.exception.AuthException;
import com.example.auth_security.auth.mapper.AuthMapper;
import com.example.auth_security.auth.request.LoginRequest;
import com.example.auth_security.auth.request.RefreshRequest;
import com.example.auth_security.auth.request.RegisterRequest;
import com.example.auth_security.auth.response.LoginResponse;
import com.example.auth_security.auth.response.RefreshTokenResponse;
import com.example.auth_security.auth.response.RegisterResponse;
import com.example.auth_security.auth.service.impl.AuthenticationServiceImpl;
import com.example.auth_security.common.mail.dto.RegisterMailDto;
import com.example.auth_security.common.mail.producer.MailQueueProducer;
import com.example.auth_security.core.security.JwtService;
import com.example.auth_security.stubs.user.UserRepositoryStub;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthMapper authMapper;

    private UserRepositoryStub userRepo;

    private User testUser;

    private MailQueueProducer mailQueueProducer;



    private AuthenticationServiceImpl authService;

    @BeforeEach
    void setUp() {

        // init stubs
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userRepo = new UserRepositoryStub(encoder);

        mailQueueProducer = mock(MailQueueProducer.class);

        authService = new AuthenticationServiceImpl(
                authenticationManager,
                userRepo,
                jwtService,
                authMapper,
                mailQueueProducer
        );

        testUser = userRepo.getUserById(UserRepositoryStub.USER_1_ID);
    }

    @Nested
    @DisplayName("Login User Tests")
    class LoginUserTests{

        @Test
        @DisplayName("Success Login User with valid credentials")
        void loginSuccessWithValidCredentials(){
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setEmail(testUser.getEmail());
            request.setPassword("pass");

            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);

            when(authentication.getPrincipal()).thenReturn(testUser);

            when(jwtService.generateAccessToken(testUser.getUsername()))
                    .thenReturn("access-token");

            when(jwtService.generateRefreshToken(testUser.getUsername()))
                    .thenReturn("refresh-token");

            LoginResponse response = new LoginResponse(
                    "access-token",
                    "refresh-token",
                    "Bearer"
            );

            when(authMapper.toLoginResponse(
                    "access-token",
                    "refresh-token",
                    "Bearer"
            )).thenReturn(response);

            // Act
            LoginResponse result = authService.login(request);

            // Assert
            assertNotNull(result);
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());
            assertEquals("Bearer", result.getTokenType());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService).generateAccessToken(testUser.getUsername());
            verify(jwtService).generateRefreshToken(testUser.getUsername());
        }

        @Test
        @DisplayName("Throw invalid email when authentication fails")
        void loginWithInvalidEmail() {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setEmail("invaild_email@example.com");
            request.setPassword("pass");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid"));

            AuthException ex = assertThrows(AuthException.class,
                    () -> authService.login(request));

            assertEquals(AuthErrorCode.BAD_CREDENTIALS.getCode(), ex.getErrorCode());
        }

        @Test
        @DisplayName("Throw invalid password when authentication fails")
        void loginWithInvalidPassword() {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setEmail(testUser.getEmail());
            request.setPassword("invalid-pass");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid"));

            AuthException ex = assertThrows(AuthException.class,
                    () -> authService.login(request));

            assertEquals(AuthErrorCode.BAD_CREDENTIALS.getCode(), ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests{
        @Test
        @DisplayName("Register user successfully with valid data")
        void registerSuccess() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setFirstName("new");
            request.setLastName("user");
            request.setEmail("newuser@test.com");
            request.setPhoneNumber("+201000000000");
            request.setPassword("password");
            request.setConfirmPassword("password");
            request.setDateOfBirth(LocalDate.now());

            User mappedUser = new User();
            mappedUser.setEmail(request.getEmail());
            mappedUser.setFirstName(request.getFirstName());
            mappedUser.setLastName(request.getLastName());
            mappedUser.setPhoneNumber(request.getPhoneNumber());
            when(authMapper.toUserEntity(request))
                    .thenReturn(mappedUser);

            when(jwtService.generateAccessToken(mappedUser.getUsername()))
                    .thenReturn("access-token");

            when(jwtService.generateRefreshToken(mappedUser.getUsername()))
                    .thenReturn("refresh-token");

            RegisterResponse response = RegisterResponse.builder()
                    .firstName(mappedUser.getFirstName())
                    .lastName(mappedUser.getLastName())
                    .phone(mappedUser.getPhoneNumber())
                    .email(mappedUser.getEmail())
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .tokenType("Bearer")
                    .build();

            when(authMapper.toRegisterResponse(
                    "access-token",
                    "refresh-token",
                    "Bearer",
                    mappedUser
            )).thenReturn(response);

            // Act
            RegisterResponse result = authService.register(request);

            // Assert
            assertNotNull(result);
            assertEquals("new", result.getFirstName());
            assertEquals("user", result.getLastName());
            assertEquals("newuser@test.com", result.getEmail());
            assertEquals("+201000000000", result.getPhone());
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());
            assertEquals("Bearer", result.getTokenType());

            verify(authMapper).toUserEntity(request);
            verify(jwtService).generateAccessToken(mappedUser.getUsername());
            verify(jwtService).generateRefreshToken(mappedUser.getUsername());
            verify(mailQueueProducer).enqueueRegisterMail(any(RegisterMailDto.class));
        }

        @Test
        @DisplayName("Register fails when email already exists")
        void registerFailWhenEmailExists() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setFirstName("new");
            request.setLastName("user");
            request.setEmail(testUser.getEmail());
            request.setPhoneNumber("+201111111111");
            request.setPassword("password");
            request.setConfirmPassword("password");
            request.setDateOfBirth(LocalDate.now());

            AuthException exception = assertThrows(
                    AuthException.class,
                    () -> authService.register(request)
            );
            assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS.getCode(), exception.getErrorCode());
            verify(authMapper, never()).toUserEntity(any());
            verify(mailQueueProducer, never()).enqueueRegisterMail(any());
            verify(jwtService, never()).generateAccessToken(any());
            verify(jwtService, never()).generateRefreshToken(any());

        }

    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class refreshTokenTests{

        @Test
        @DisplayName("Refresh token successfully returns new access token")
        void refreshTokenSuccess() {
            // Arrange
            RefreshRequest request = new RefreshRequest();
            request.setRefreshToken("valid-refresh-token");
            when(jwtService.refreshAccessToken("valid-refresh-token"))
                    .thenReturn("new-access-token");

            RefreshTokenResponse response = RefreshTokenResponse.builder()
                    .accessToken("new-access-token")
                    .tokenType("Bearer")
                    .build();

            when(authMapper.toRefreshTokenResponse("new-access-token", "Bearer"))
                    .thenReturn(response);

            // Act
            RefreshTokenResponse result = authService.refreshToken(request);

            // Assert
            assertNotNull(result);
            assertEquals("new-access-token", result.getAccessToken());
            assertEquals("Bearer", result.getTokenType());

            verify(jwtService).refreshAccessToken("valid-refresh-token");
            verify(authMapper).toRefreshTokenResponse("new-access-token", "Bearer");
        }

        @Test
        @DisplayName("Refresh token fails when refresh token is invalid")
        void refreshTokenFailsWhenInvalid() {
            // Arrange
            RefreshRequest request = new RefreshRequest();
            request.setRefreshToken("invalid-token");

            when(jwtService.refreshAccessToken("invalid-token"))
                    .thenThrow(new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

            // Act + Assert
            AuthException exception = assertThrows(
                    AuthException.class,
                    () -> authService.refreshToken(request)
            );

            assertEquals(AuthErrorCode.INVALID_REFRESH_TOKEN.getCode(), exception.getErrorCode());

            verify(jwtService).refreshAccessToken("invalid-token");
            verify(authMapper, never()).toRefreshTokenResponse(any(), any());
        }
    }


}
