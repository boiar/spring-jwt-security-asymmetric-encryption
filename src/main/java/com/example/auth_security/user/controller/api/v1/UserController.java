package com.example.auth_security.user.controller.api.v1;

import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.request.ChangePasswordRequest;
import com.example.auth_security.user.request.ProfileUpdateRequest;
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



    @PatchMapping("update-profile")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateProfile(
            @RequestBody
            @Valid
            final ProfileUpdateRequest request,
            final Authentication principal) {

        this.userService.updateProfileInfo(request, getUserId(principal));
    }

    @PostMapping("update-password")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody
            @Valid
            final ChangePasswordRequest request,
            final Authentication principal){
        this.userService.changePassword(request, getUserId(principal));
    }

    @PatchMapping("profile/deactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deactivateAccount(final Authentication principal) {
        this.userService.deactivateAccount(getUserId(principal));
    }

    @PatchMapping("/profile/reactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reactivateAccount(final Authentication principal) {
        this.userService.reactivateAccount(getUserId(principal));
    }

    @DeleteMapping("/profile")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAccount(final Authentication principal) {
        this.userService.deleteAccount(getUserId(principal));
    }

    private String getUserId(final Authentication authentication){
        return ((User) Objects.requireNonNull(authentication.getPrincipal())).getId();
    }

}
