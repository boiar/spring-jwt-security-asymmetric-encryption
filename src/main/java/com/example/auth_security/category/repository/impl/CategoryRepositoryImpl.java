package com.example.auth_security.category.repository.impl;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.category.repository.jpa.CategoryRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryRepositoryJpa jpa;

    @Override
    public boolean existsByNameForUser(String name, String userId) {
        return jpa.existsByNameForUser(name, userId);
    }

    @Override
    public List<Category> findAllForUser(String userId) {
        return jpa.findAllForUser(userId);
    }

    @Override
    public Optional<Category> findByIdForUser(Long categoryId, String userId) {
        return jpa.findByIdForUser(categoryId, userId);
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return jpa.findById(categoryId);
    }

    @Override
    public Category save(Category category) {
        return jpa.save(category);
    }
}
