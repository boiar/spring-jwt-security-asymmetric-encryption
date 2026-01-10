package com.example.auth_security.stubs.category;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.stubs.user.UserRepositoryStub;
import com.example.auth_security.user.entity.User;

import java.util.*;

public class CategoryRepositoryStub implements CategoryRepository {

    private final Map<Long, Category> store = new HashMap<>();
    private long currentId = 3L;

    public static final Long CAT_1_ID = 1L;
    public static final Long CAT_2_ID = 2L;

    public CategoryRepositoryStub(){
        seedCategories();
    }

    private void seedCategories() {

        EntityAuditActorData actorDataCategory = new EntityAuditActorData();
        actorDataCategory.setCreatedBy(UserRepositoryStub.USER_1_ID); // use test user
        actorDataCategory.setLastModifiedBy(UserRepositoryStub.USER_1_ID);

        Category testCategory = Category.builder()
                .id(1L)
                .name("Work")
                .description("Work todos")
                .timingData(new EntityAuditTimingData())
                .actorData(actorDataCategory)
                .build();

        Category anotherCategory = Category.builder()
                .id(2L)
                .name("Personal")
                .description("Personal todos")
                .timingData(new EntityAuditTimingData())
                .actorData(actorDataCategory)
                .build();


        store.put(testCategory.getId(), testCategory);
        store.put(anotherCategory.getId(), anotherCategory);
    }

    public Category getCategoryById(Long categoryId) {
        return store.get(categoryId);
    }


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

    public void clear() {
        store.clear();
    }

    public int count() {
        return store.size();
    }
}
