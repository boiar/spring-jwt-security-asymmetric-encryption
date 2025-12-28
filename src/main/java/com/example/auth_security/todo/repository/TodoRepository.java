package com.example.auth_security.todo.repository;

import com.example.auth_security.todo.entity.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    Todo save(Todo todo);

    Optional<Todo> findById(Long todoId);

    List<Todo> findAllTodayTodosByUserId(String userId);


    List<Todo> findAllByUserIdAndCategoryId(String userId, Long categoryId);

    List<Todo> findAllDueTodos(String userId);

    void deleteById(Long todoId);

}
