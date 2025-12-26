package com.example.auth_security.category.repository.jpa;

import com.example.auth_security.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryJpa extends JpaRepository<Category, Long> {

    @Query("""
        SELECT COUNT(c) > 0
        FROM Category c
        WHERE LOWER(c.name) = LOWER(:name)
        AND (c.actorData.createdBy = :userId OR c.actorData.createdBy = 'APP')
    """)
    boolean existsByNameForUser(String name, String userId);

    @Query("""
           SELECT cat
           FROM Category cat
           WHERE cat.actorData.createdBy = :userId OR cat.actorData.createdBy = 'APP' 
           """)
    List<Category> findAllForUser(String userId);

    @Query("""
            SELECT c FROM Category c
            WHERE c.id = :categoryId
            AND (c.actorData.createdBy = :userId OR c.actorData.createdBy = 'APP')
            """)
    Optional<Category> findByIdForUser(Long categoryId, String userId);
}
