package com.example.auth_security.user.service.interfaces;

import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.request.ChangePasswordRequest;
import com.example.auth_security.user.request.ProfileUpdateRequest;
import com.example.auth_security.user.response.UserProfileResponse;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {

    void updateProfileInfo(ProfileUpdateRequest req, String userId);
    void changePassword(ChangePasswordRequest req, String userId);
    void deactivateAccount(String userId);
    void reactivateAccount(String userId);
    void deleteAccount(String userId);

    UserProfileResponse getUserById(String userId);


}
