package com.example.auth_security.todo.service.impl;

import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.mapper.TodoMapper;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.todo.service.interfaces.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;



    @Override
    public String createTodo(final CreateTodoRequest request, final String userId) {
        final Todo todo = this.todoMapper.toTodoEntity(request);

        return null;
    }

    @Override
    public void updateTodo(final UpdateTodoRequest request, final String userId) {

    }

    @Override
    public void findTodoById(String todoId) {

    }

    @Override
    public void findAllTodosForToday(String todoId) {

    }

    @Override
    public void findAllDueTodos(String userId) {

    }

    @Override
    public void deleteTodoById(String todoId) {

    }
}
