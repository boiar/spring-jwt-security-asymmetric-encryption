package com.example.auth_security.user.controller.api.v1;

import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.request.ChangePasswordRequest;
import com.example.auth_security.user.request.ProfileUpdateRequest;
import com.example.auth_security.user.response.UserProfileResponse;
import com.example.auth_security.user.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API V1")
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    @ResponseStatus(code = HttpStatus.OK)
    public UserProfileResponse getProfile(final Authentication principal) {
        return this.userService.getUserById(getUserId(principal));
    }

    @PatchMapping("update-profile")
    @ResponseStatus(code = HttpStatus.OK)
    public void updateProfile(
            @RequestBody
            @Valid
            final ProfileUpdateRequest request,
            final Authentication principal) {

        this.userService.updateProfileInfo(request, getUserId(principal));
    }

    @PostMapping("update-password")
    @ResponseStatus(code = HttpStatus.OK)
    public void changePassword(
            @RequestBody
            @Valid
            final ChangePasswordRequest request,
            final Authentication principal){
        this.userService.changePassword(request, getUserId(principal));
    }

    @PatchMapping("profile/deactivate")
    @ResponseStatus(code = HttpStatus.OK)
    public void deactivateAccount(final Authentication principal) {
        this.userService.deactivateAccount(getUserId(principal));
    }

    @PatchMapping("/profile/reactivate")
    @ResponseStatus(code = HttpStatus.OK)
    public void reactivateAccount(final Authentication principal) {
        this.userService.reactivateAccount(getUserId(principal));
    }

    @DeleteMapping("/delete")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteAccount(final Authentication principal) {
        this.userService.deleteAccount(getUserId(principal));
    }

    private String getUserId(final Authentication authentication){
        return ((User) Objects.requireNonNull(authentication.getPrincipal())).getId();
    }

}
