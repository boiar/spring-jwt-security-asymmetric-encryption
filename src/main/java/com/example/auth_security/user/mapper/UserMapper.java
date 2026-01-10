package com.example.auth_security.user.mapper;

import com.example.auth_security.todo.response.TodoResponse;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.request.ProfileUpdateRequest;
import com.example.auth_security.user.response.UserProfileResponse;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {


    public void mergeUserInfo(final User user, final ProfileUpdateRequest request) {
        if (StringUtils.isNotBlank(request.getFirstName()) && !user.getFirstName().equals(request.getFirstName())) {
            user.setFirstName(request.getFirstName());
        }

        if (StringUtils.isNotBlank(request.getLastName()) && !user.getLastName().equals(request.getLastName())) {
            user.setLastName(request.getLastName());
        }

        if (request.getDateOfBirth() != null && !user.getDateOfBirth().equals(request.getDateOfBirth())) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
    }

    public UserProfileResponse toUserProfileResponse(final User user) {
        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
