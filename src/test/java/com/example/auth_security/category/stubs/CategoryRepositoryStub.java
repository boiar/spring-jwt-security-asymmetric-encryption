package com.example.auth_security.category.stubs;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;

import java.util.*;

public class CategoryRepositoryStub implements CategoryRepository {

    private final Map<Long, Category> store = new HashMap<>();
    private long currentId = 1L;

    @Override
    public boolean existsByNameForUser(String name, String userId) {
        return store.values().stream()
                .anyMatch(c -> name.equals(c.getName()) && userId.equals(c.getActorData().getCreatedBy()));
    }

    @Override
    public List<Category> findAllForUser(String userId) {
        List<Category> result = new ArrayList<>();
        for (Category c : store.values()) {
            if (userId.equals(c.getActorData().getCreatedBy())) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public Optional<Category> findByIdForUser(Long categoryId, String userId) {
        Category category = store.get(categoryId);
        if (category != null && userId.equals(category.getActorData().getCreatedBy())) {
            return Optional.of(category);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Category> findById(Long catId) {
        return Optional.ofNullable(store.get(catId));
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(currentId++);
        }
        store.put(category.getId(), category);
        return category;
    }
}
