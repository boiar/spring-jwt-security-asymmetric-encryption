package com.example.auth_security.user.repository;

import com.example.auth_security.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailIgnoreCase(String email);




}
