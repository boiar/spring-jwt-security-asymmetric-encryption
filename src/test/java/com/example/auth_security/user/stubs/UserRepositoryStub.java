package com.example.auth_security.user.stubs;

import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.repository.TodoRepository;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryStub implements UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();


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
