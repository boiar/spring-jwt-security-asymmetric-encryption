package com.example.auth_security.category.repository;

import com.example.auth_security.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    @Query("""
            SELECT count(cat)
            FROM Category cat
            WHERE LOWER(cat.name) = LOWER(:name)
            AND cat.actorData.createdBy = :userId OR cat.actorData.createdBy = "APP"
            
        """)
    Long findByNameAndUserId(String name, String userId);

    @Query("""
           SELECT cat
           FROM Category cat
           WHERE cat.actorData.createdBy = :userId OR cat.actorData.createdBy = "APP" 
           """)
    List<Category> findAllCategoriesByUserId(String userId);

    @Query("""
            SELECT c FROM Category c
            WHERE c.id = :categoryId
            AND (c.actorData.createdBy = :userId OR c.actorData.createdBy = 'APP')
            """)
    Optional<Category> findByCategoryIdAndUserId(String categoryId, String userId);
}
