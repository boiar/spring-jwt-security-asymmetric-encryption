package com.example.auth_security.user.service.impl;

import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.mapper.UserMapper;
import com.example.auth_security.user.repository.UserRepository;
import com.example.auth_security.user.service.interfaces.UserService;
import com.example.auth_security.user.request.ChangePasswordRequest;
import com.example.auth_security.user.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return this.userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found !"));
    }


    @Override
    public void updateProfileInfo(ProfileUpdateRequest req, String userId) {
        User userObj = this.userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found !"));

        userObj.

    }

    @Override
    public void changePassword(ChangePasswordRequest req, String userId) {

    }

    @Override
    public void deactivateAccount(String userId) {

    }

    @Override
    public void reactivateAccount(String userId) {

    }

    @Override
    public void deleteAccount(String userId) {

    }


}
