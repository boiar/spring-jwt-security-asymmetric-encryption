package com.example.auth_security.common.service.impl;

import com.example.auth_security.common.exception.CommonException;
import com.example.auth_security.common.service.interfaces.CurrentUserService;
import com.example.auth_security.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    @Override
    public String getUsername() {
        Authentication authentication = getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }

    @Override
    public String getUserId() {
        Authentication authentication = getAuthentication();
        return  ((User) authentication.getPrincipal()).getId();
    }


    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
