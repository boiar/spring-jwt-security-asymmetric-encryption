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
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImplTest Unit Test")
class UserServiceImplTest {
    private final UserMapper userMapper = new UserMapper();

    private UserRepositoryStub userRepo;
    private UserServiceImpl userService;
    private User testUser;
    private ChangePasswordRequest changePasswordRequest;
    private ProfileUpdateRequest profileUpdateRequest;
    private UserProfileResponse userProfileResponse;


    @BeforeEach
    void setUp() {
        changePasswordRequest = new ChangePasswordRequest();
        profileUpdateRequest = new ProfileUpdateRequest();
        userProfileResponse = new UserProfileResponse();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // init stubs
        userRepo = new UserRepositoryStub(passwordEncoder);

        // inject dependencies including the stub
        userService = new UserServiceImpl(userRepo, passwordEncoder, userMapper);
        testUser = userRepo.getUserById(UserRepositoryStub.USER_1_ID);
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
            profileUpdateRequest.setFirstName("Jane");
            profileUpdateRequest.setLastName("Smith");
            profileUpdateRequest.setDateOfBirth(LocalDate.now());

            // ===== when =====
            userService.updateProfileInfo(profileUpdateRequest, userId);

            // ===== then =====
            Optional<User> updatedUserObj = userRepo.findById(userId);
            assertTrue(updatedUserObj.isPresent(), "Updated todo should exist in repository");

            User updatedTodo = updatedUserObj.get();


            // Verify all fields were updated correctly
            assertEquals(profileUpdateRequest.getFirstName(), updatedTodo.getFirstName(), "First Name should be updated");
            assertEquals(profileUpdateRequest.getLastName(), updatedTodo.getLastName(), "Last Name should be updated");
            assertEquals(profileUpdateRequest.getDateOfBirth(), updatedTodo.getDateOfBirth(), "Date of birth should be updated");
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

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests{

        @Test
        @DisplayName("Should change password successfully when valid request")
        void shouldChangePasswordSuccessfully() {
            // Arrange
            changePasswordRequest.setCurrentPassword("pass"); // matches seeded password
            changePasswordRequest.setNewPassword("newPass123");
            changePasswordRequest.setConfirmPassword("newPass123");


            // Act
            userService.changePassword(changePasswordRequest, testUser.getId());
            // Assert
            User updated = userRepo.getUserById(testUser.getId());
            BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();
            assertTrue(realEncoder.matches("newPass123", updated.getPassword()),
                    "Password should be updated and encoded");

        }

        @Test
        @DisplayName("Should throw exception when new password and confirm password mismatch")
        void shouldThrowMismatchException() {
            changePasswordRequest.setCurrentPassword("pass");
            changePasswordRequest.setNewPassword("newPass123");
            changePasswordRequest.setConfirmPassword("differentPass");

            UserException ex = assertThrows(UserException.class,
                    () -> userService.changePassword(changePasswordRequest, testUser.getId()));

            assertEquals(UserErrorCode.CHANGE_PASSWORD_MISMATCH.getCode(), ex.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when current password is invalid")
        void shouldThrowInvalidCurrentPassword() {
            changePasswordRequest.setCurrentPassword("wrongPass");
            changePasswordRequest.setNewPassword("newPass123");
            changePasswordRequest.setConfirmPassword("newPass123");

            UserException ex = assertThrows(UserException.class,
                    () -> userService.changePassword(changePasswordRequest, testUser.getId()));

            assertEquals(UserErrorCode.INVALID_CURRENT_PASSWORD.getCode(), ex.getErrorCode());
        }

    }

    @Nested
    @DisplayName("Deactivate Account Tests")
    class DeactivateAccountTests {

        @Test
        @DisplayName("Should deactivate account successfully when user is enabled")
        void shouldDeactivateAccountSuccessfully() {
            // Arrange
            String userId = testUser.getId();
            assertTrue(testUser.isEnabled(), "User should initially be enabled");

            // Act
            userService.deactivateAccount(userId);

            // Assert
            User updated = userRepo.getUserById(userId);
            assertFalse(updated.isEnabled(), "User should now be disabled");
        }

        @Test
        @DisplayName("Should throw exception if account already deactivated")
        void shouldThrowIfAlreadyDeactivated() {
            String userId = testUser.getId();
            testUser.setEnabled(false); //disable user
            userRepo.save(testUser);

            // Act & Assert
            UserException ex = assertThrows(
                    UserException.class,
                    () -> userService.deactivateAccount(userId)
            );

            assertEquals(UserErrorCode.ACCOUNT_ALREADY_DEACTIVATED.getCode(), ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Reactivate Account Tests")
    class ReactivateAccountTests {
        @Test
        @DisplayName("Should reactivate account successfully when user is disabled")
        void shouldReactivateAccountSuccessfully() {
            // Arrange
            String userId = testUser.getId();
            testUser.setEnabled(false); // ensure user is disabled
            userRepo.save(testUser);
            assertFalse(testUser.isEnabled(), "User should initially be disabled");

            // Act
            userService.reactivateAccount(userId);

            // Assert
            User updated = userRepo.getUserById(userId);
            assertTrue(updated.isEnabled(), "User should now be enabled");
        }

        @Test
        @DisplayName("Should throw exception if account already active")
        void shouldThrowIfAlreadyActive() {
            // Arrange
            String userId = testUser.getId();
            testUser.setEnabled(true); // ensure user is already active
            userRepo.save(testUser);

            // Act & Assert
            UserException ex = assertThrows(
                    UserException.class,
                    () -> userService.reactivateAccount(userId)
            );

            assertEquals(UserErrorCode.ACCOUNT_ALREADY_ACTIVATED.getCode(), ex.getErrorCode());
        }
    }


    @Nested
    @DisplayName("Get User By ID Tests")
    class GetUserByIdTests {
        @Test
        @DisplayName("Should return user profile when user exists")
        void shouldReturnUserProfileWhenUserExists() {
            // Arrange
            String userId = testUser.getId();

            // Act
            UserProfileResponse response = userService.getUserById(userId);

            // Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testUser.getFirstName(), response.getFirstName(), "First name should match");
            assertEquals(testUser.getLastName(), response.getLastName(), "Last name should match");
            assertEquals(testUser.getEmail(), response.getEmail(), "Email should match");
        }

        @Test
        @DisplayName("Should throw exception if user not found")
        void shouldThrowIfUserNotFound() {
            // Arrange
            String invalidUserId = "non-existent-id";

            // Act & Assert
            UserException ex = assertThrows(
                    UserException.class,
                    () -> userService.getUserById(invalidUserId)
            );

            assertEquals(UserErrorCode.USER_NOT_FOUND.getCode(), ex.getErrorCode(),
                    "Error code should be USER_NOT_FOUND");
        }
    }


}