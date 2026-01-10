package com.example.auth_security.stubs.user;

import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryStub implements UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();


    private final PasswordEncoder passwordEncoder;

    public static final String USER_1_ID = "user-1";
    public static final String USER_2_ID = "user-2";

    public UserRepositoryStub(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        seedUsers();
    }

    private void seedUsers() {

        if (passwordEncoder == null) {
            throw new IllegalStateException("PasswordEncoder must be set before seeding users");
        }

        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy("user");

        User testUser = User.builder()
                .id(USER_1_ID)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("0123456789")
                .password(passwordEncoder.encode("pass"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .enabled(true)
                .timingData(new EntityAuditTimingData())
                .actorData(actorData)
                .build();

        User anotherUser = User.builder()
                .id(USER_2_ID)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("0123456781")
                .password(this.passwordEncoder.encode("pass"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .enabled(true)
                .timingData(new EntityAuditTimingData())
                .actorData(actorData)
                .build();

        store.put(testUser.getId(), testUser);
        store.put(anotherUser.getId(), anotherUser);
    }

    public User getUserById(String userId) {
        return store.get(userId);
    }

    /* Main Methods */

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return store.values().stream()
                .anyMatch(user ->
                    user.getEmail() != null &&
                    user.getEmail().equalsIgnoreCase(email)
                );
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return store.values().stream()
                .anyMatch(user ->
                        Objects.equals(user.getPhoneNumber(), phoneNumber)
                );
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return store.values().stream()
                .filter(user ->
                        user.getEmail() != null &&
                                user.getEmail().equalsIgnoreCase(email)
                )
                .findFirst();
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        store.put(user.getId(), user);
    }


    public void clear() {
        store.clear();
    }

    public int count() {
        return store.size();
    }


}
