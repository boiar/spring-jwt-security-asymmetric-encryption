package com.example.auth_security.todo.repository.impl;

import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.repository.TodoRepository;
import com.example.auth_security.todo.repository.jpa.TodoRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepository {

    private final TodoRepositoryJpa jpa;


    @Override
    public Todo save(Todo todo) {
       return this.jpa.save(todo);
    }

    @Override
    public Optional<Todo> findById(Long todoId) {
        return jpa.findById(todoId);
    }

    @Override
    public List<Todo> findAllTodayTodosByUserId(String userId) {
        return jpa.findAllTodayTodosByUserId(userId);
    }

    @Override
    public List<Todo> findAllByUserIdAndCategoryId(String userId, Long categoryId) {
        return jpa.findAllByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Todo> findAllDueTodos(String userId) {
        return this.jpa.findAllDueTodos(userId);
    }

    @Override
    public void deleteById(Long todoId) {
        this.jpa.deleteById(todoId);
    }


}
