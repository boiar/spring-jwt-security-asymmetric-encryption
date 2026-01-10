package com.example.auth_security.integration.user;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.core.security.JwtService;
import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import com.example.auth_security.user.request.ChangePasswordRequest;
import com.example.auth_security.user.request.ProfileUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;


    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
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

    @Test
    @DisplayName("Get user profile endpoint V1 Tests")
    void shouldGetProfile() throws Exception {

            mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"));

    }

    @Test
    @DisplayName("Update user profile endpoint V1 Tests")
    void shouldUpdateProfile() throws Exception {

        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();

        profileUpdateRequest.setFirstName("updated test");
        profileUpdateRequest.setLastName("updated");
        profileUpdateRequest.setDateOfBirth(LocalDate.now());

        mockMvc.perform(patch("/api/v1/users/update-profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdateRequest)))
                .andExpect(status().isOk());

        User updated = userRepo.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getFirstName()).isEqualTo("updated test");
        assertThat(updated.getLastName()).isEqualTo("updated");
        assertThat(updated.getDateOfBirth()).isEqualTo(LocalDate.now());

    }

    @Test
    @DisplayName("Update user password endpoint V1 Tests")
    void shouldUpdateUserPassword() throws Exception {

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();

        changePasswordRequest.setCurrentPassword("pass");
        changePasswordRequest.setNewPassword("p@ssw0rd_!12");
        changePasswordRequest.setConfirmPassword("p@ssw0rd_!12");

        mockMvc.perform(post("/api/v1/users/update-password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        User updated = userRepo.findById(testUser.getId()).orElseThrow();

        assertTrue(
                this.passwordEncoder.matches(
                        changePasswordRequest.getNewPassword(),
                        updated.getPassword()
                ),
                "Password should be updated and encoded"
        );

    }


    @Test
    @DisplayName("Not Update user password with invalid password")
    void shouldNotUpdateUserPasswordWithInvalidCurrentPassword() throws Exception {

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();

        changePasswordRequest.setCurrentPassword("invalid-current-pass");
        changePasswordRequest.setNewPassword("p@ssw0rd_!12");
        changePasswordRequest.setConfirmPassword("p@ssw0rd_!12");

        mockMvc.perform(post("/api/v1/users/update-password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Deactivate user")
    void shouldDeactivateUser() throws Exception {


        mockMvc.perform(patch("/api/v1/users/profile/deactivate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        User updatedUser = userRepo.findById(testUser.getId()).orElseThrow();
        assertFalse(updatedUser.isEnabled(), "User should be deactivated");

    }

    @Test
    @DisplayName("Reactivate user")
    void shouldReactivateUser() throws Exception {

        testUser.setEnabled(false);
        this.userRepo.save(testUser);

        mockMvc.perform(patch("/api/v1/users/profile/reactivate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        User updatedUser = userRepo.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.isEnabled(), "User should be reactivated");

    }





}
