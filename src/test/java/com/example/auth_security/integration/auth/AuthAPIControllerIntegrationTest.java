package com.example.auth_security.integration.auth;

import com.example.auth_security.auth.request.LoginRequest;
import com.example.auth_security.auth.request.RegisterRequest;
import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.core.security.JwtService;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthAPIControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Category testCategory;
    private Category anotherTestCategory;
    private String accessToken;

    @BeforeEach
    void setUp(){

        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy("user");

        this.testUser = User.builder()
                .firstName("Test")
                .lastName("test")
                .email("test@example.com")
                .phoneNumber("0123456789")
                .password(this.passwordEncoder.encode("pass"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .enabled(true)
                .timingData(new EntityAuditTimingData())
                .actorData(actorData)
                .build();

        userRepo.save(testUser);
        this.accessToken = this.jwtService.generateAccessToken(testUser.getUsername());
    }


    @Nested
    @DisplayName("Login V1 Tests")
    class LoginV1Tests {
        @Test
        @DisplayName("Success login User Endpoint V1 Tests")
        void shouldLogin() throws Exception {
            // Given
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("pass");
            // When & Then
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").isString())
                    .andExpect(jsonPath("$.refresh_token").isString())
                    .andExpect(jsonPath("$.token_type").isString())
                    .andReturn();

        }
        @Test
        @DisplayName("Fail login User with invalid pass")
        void shouldNotLoginWithInvalidPassword() throws Exception {
            // Given
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("invalid-pass");


            // When & Then
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

        }

        @Test
        @DisplayName("Fail login User with invalid email")
        void shouldNotLoginWithInvalidEmail() throws Exception {
            // Given
            LoginRequest request = new LoginRequest();
            request.setEmail("invalid-email@example.com");
            request.setPassword("pass");


            // When & Then
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

        }
    }


    @Nested
    @DisplayName("Login V1 Tests")
    class RegisterV1Tests {
        @Test
        @DisplayName("Success register user Endpoint V1 Tests")
        void shouldRegister() throws Exception {
            // Given
            RegisterRequest request = new RegisterRequest();
            request.setFirstName("Test");
            request.setLastName("test");
            request.setEmail("real-test@example.com");
            request.setPhoneNumber("+20123456781");
            request.setPassword("pAssw0rd1!_");
            request.setConfirmPassword("pAssw0rd1!_");
            request.setDateOfBirth(LocalDate.of(1990, 1, 1));


            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Fail register with exist email user Endpoint V1 Tests")
        void shouldFailRegister() throws Exception {
            // Given
            RegisterRequest request = new RegisterRequest();
            request.setFirstName("Test");
            request.setLastName("test");
            //exist email
            request.setEmail("test@example.com");
            request.setPhoneNumber("+20123456781");
            request.setPassword("pAssw0rd1!_");
            request.setConfirmPassword("pAssw0rd1!_");
            request.setDateOfBirth(LocalDate.of(1990, 1, 1));


            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }




    }




}
