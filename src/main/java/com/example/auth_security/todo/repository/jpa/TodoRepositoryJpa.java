package com.example.auth_security.todo.repository.jpa;

import com.example.auth_security.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TodoRepositoryJpa extends JpaRepository<Todo, Number> {

    @Query("""
            SELECT t FROM Todo t
            WHERE t.user.id = :userId
            AND t.startDate = CURRENT_DATE
            """)
    List<Todo> findAllTodayTodosByUserId(String userId);

    List<Todo> findAllByUserIdAndCategoryId(String userId, Long categoryId);

    @Query("""
            SELECT t FROM Todo t
            WHERE t.endDate >= CURRENT_DATE AND t.endTime >= CURRENT_TIME
            """)
    List<Todo> findAllDueTodos(String userId);

}
