package com.example.auth_security.todo.service.interfaces;

import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;

import java.util.List;

public interface TodoService {

    String createTodo(CreateTodoRequest request, String userId);
    void updateTodo(UpdateTodoRequest request, String userId);
    void findTodoById(String todoId);
    void findAllTodosForToday(String todoId);

    void findAllDueTodos(String userId);
    void deleteTodoById(String todoId);


}
