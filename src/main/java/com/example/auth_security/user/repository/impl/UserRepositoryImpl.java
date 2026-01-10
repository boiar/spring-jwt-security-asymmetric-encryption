package com.example.auth_security.user.repository.impl;

import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import com.example.auth_security.user.repository.jpa.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return this.userRepositoryJpa.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return this.userRepositoryJpa.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return this.userRepositoryJpa.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findById(String userId) {
        return this.userRepositoryJpa.findById(userId);
    }

    @Override
    public void save(User userObj) {
        this.userRepositoryJpa.save(userObj);
    }
}
