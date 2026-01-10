package com.example.auth_security.service.user;

import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.exception.UserErrorCode;
import com.example.auth_security.user.exception.UserException;
import com.example.auth_security.user.mapper.UserMapper;
import com.example.auth_security.user.repository.UserRepository;
import com.example.auth_security.user.request.ChangePasswordRequest;
import com.example.auth_security.user.request.ProfileUpdateRequest;
import com.example.auth_security.user.response.UserProfileResponse;
import com.example.auth_security.user.service.impl.UserServiceImpl;
import com.example.auth_security.stubs.user.UserRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImplTest Unit Test")
class UserServiceImplTest {
    private final UserMapper userMapper = new UserMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepo; // stub
    private UserServiceImpl userService;
    private User testUser;
    private ChangePasswordRequest changePasswordRequest;
    private ProfileUpdateRequest profileUpdateRequest;
    private UserProfileResponse userProfileResponse;




    @BeforeEach
    void setUp() {

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        // init stubs
        userRepo = new UserRepositoryStub(encoder);

        // inject dependencies including the stub
        userService = new UserServiceImpl(userRepo, passwordEncoder, userMapper);


        testUser = User.builder()
                .id("user-123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded-old-pass")
                .enabled(true)
                .build();

        userRepo.save(testUser);
    }


    @Nested
    @DisplayName("Load User By Username Tests")
    class loadUserByUsername{
        @Test
        @DisplayName("Should return user when email exists")
        void shouldReturnUserWhenEmailExists() {
            String email = "john.doe@example.com";
            UserDetails result = userService.loadUserByEmail(email);

            assertNotNull(result);
            assertEquals(email, result.getUsername());
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when email does not exist")
        void shouldThrowExceptionWhenEmailNotFound() {
            String email = "not.exists@example.com";

            UsernameNotFoundException ex =
                    assertThrows(
                            UsernameNotFoundException.class,
                            () -> userService.loadUserByEmail(email)
                    );

            assertEquals("User not found !", ex.getMessage());
        }

    }



    @Nested
    @DisplayName("Update User Profile Tests")
    class UpdateProfileInfo{
        @Test
        @DisplayName("Should update user profile successfully")
        void shouldUpdateProfileInfo() {
            // arrange
            String userId = testUser.getId();
            ProfileUpdateRequest request = new ProfileUpdateRequest();
            request.setFirstName("Jane");
            request.setLastName("Smith");
            request.setDateOfBirth(LocalDate.now());

            // ===== when =====
            userService.updateProfileInfo(request, userId);

            // ===== then =====
            Optional<User> updatedUserObj = userRepo.findById(userId);
            assertTrue(updatedUserObj.isPresent(), "Updated todo should exist in repository");

            User updatedTodo = updatedUserObj.get();


            // Verify all fields were updated correctly
            assertEquals(request.getFirstName(), updatedTodo.getFirstName(), "First Name should be updated");
            assertEquals(request.getLastName(), updatedTodo.getLastName(), "Last Name should be updated");
            assertEquals(request.getDateOfBirth(), updatedTodo.getDateOfBirth(), "Date of birth should be updated");
        }

        @Test
        @DisplayName("Should throw exception if user not found")
        void shouldThrowIfUserNotFound() {
            ProfileUpdateRequest request = new ProfileUpdateRequest();

            UserException ex = assertThrows(UserException.class,
                    () -> userService.updateProfileInfo(request, "invalid-user-id"));

            assertEquals(UserErrorCode.USER_NOT_FOUND.getCode(), ex.getErrorCode());
        }
    }



}