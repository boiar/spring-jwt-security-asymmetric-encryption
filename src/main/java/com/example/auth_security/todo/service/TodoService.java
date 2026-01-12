package com.example.auth_security.todo.service;

import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.todo.response.TodoResponse;

import java.util.List;

public interface TodoService {

    Long createTodo(CreateTodoRequest request, String userId);
    void updateTodo(UpdateTodoRequest request, Long todoId, String userId);
    TodoResponse findTodoById(Long todoId);
    List<TodoResponse> findAllTodosForToday(final String userId);
    List<TodoResponse> findAllTodosByCategory(Long catId, String userId);


    List<TodoResponse> findAllDueTodos(String userId);
    void deleteTodoById(Long todoId);


}
