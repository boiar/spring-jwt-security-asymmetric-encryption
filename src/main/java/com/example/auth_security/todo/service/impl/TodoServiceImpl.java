package com.example.auth_security.todo.service.impl;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.category.service.interfaces.CategoryService;
import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.exception.TodoErrorCode;
import com.example.auth_security.todo.exception.TodoException;
import com.example.auth_security.todo.mapper.TodoMapper;
import com.example.auth_security.todo.repository.TodoRepository;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.todo.response.TodoResponse;
import com.example.auth_security.todo.service.interfaces.TodoService;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;
    private final TodoRepository todoRepo;
    private final CategoryService categoryService;
    private final UserService userService;



    @Override
    public Long createTodo(final CreateTodoRequest request, final String userId) {

        final Category category = this.categoryService.checkAndReturnCategory(request.getCategoryId(), userId);
        final User user = this.userService.getUserById(userId);
        final Todo todo = this.todoMapper.toTodoEntity(request);
        todo.setCategory(category);
        todo.setUser(user);
        todo.getActorData().setCreatedBy(userId);
        return this.todoRepo.save(todo).getId();
    }

    @Override
    public void updateTodo(final UpdateTodoRequest request, Long todoId, final String userId) {

        final Todo todoToUpdate = this.todoRepo.findById(todoId).orElseThrow (()->
                                                        new TodoException(TodoErrorCode.TODO_NOT_EXISTS));

        final Category category = this.categoryService.checkAndReturnCategory(request.getCategoryId(), userId);

        this.todoMapper.mergerTodoEntity(todoToUpdate, request);
        todoToUpdate.setCategory(category);


        this.todoRepo.save(todoToUpdate);

    }

    @Override
    public TodoResponse findTodoById(Long todoId) {

        return this.todoRepo.findById(todoId)
                .map(this.todoMapper::toTodoResponse)
                .orElseThrow(() ->
                        new TodoException(TodoErrorCode.TODO_NOT_EXISTS));

    }


    @Override
    public List<TodoResponse> findAllTodosForToday(final String userId) {
        return this.todoRepo.findAllTodayTodosByUserId(userId)
                .stream()
                .map(this.todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findAllTodosByCategory(Long catId, String userId) {
        return this.todoRepo.findAllByUserIdAndCategoryId(userId, catId)
                .stream()
                .map(this.todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findAllDueTodos(String userId) {
        return this.todoRepo.findAllDueTodos(userId)
                .stream()
                .map(this.todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public void deleteTodoById(Long todoId) {
        this.todoRepo.deleteById(todoId);
    }


}
