package com.example.auth_security.todo.repository;

import com.example.auth_security.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepo extends JpaRepository<Todo, Number> {
}
