package com.example.auth_security.category.repository;

import com.example.auth_security.category.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    boolean existsByNameForUser(String name, String userId);

    List<Category> findAllForUser(String userId);

    Optional<Category> findByIdForUser(Long categoryId, String userId);

    Optional<Category> findById(Long catId);
    Category save(Category category);


}
