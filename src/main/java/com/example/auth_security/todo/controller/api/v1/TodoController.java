package com.example.auth_security.todo.controller.api.v1;


import com.example.auth_security.common.service.interfaces.CurrentUserService;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.todo.response.CreateTodoResponse;
import com.example.auth_security.todo.response.TodoResponse;
import com.example.auth_security.todo.service.interfaces.TodoService;
import com.example.auth_security.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Tag(name= "Todos", description = "Todo API v1")
public class TodoController {

    private final TodoService todoService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<CreateTodoResponse> createTodo(
            @RequestBody
            @Valid
            final CreateTodoRequest request
    ) {
        final String userId = currentUserService.getUserId();
        final Long todoId = this.todoService.createTodo(request, userId);
        return ResponseEntity.status(CREATED).body(new CreateTodoResponse(todoId));
    }

    @PutMapping("/{todo-id}")
    public ResponseEntity<Void> updateTodo(
            @RequestBody
            @Valid
            final UpdateTodoRequest updateTodoRequest,
            @PathVariable("todo-id")
            final Long todoId
    ) {
        final String userId = currentUserService.getUserId();
        this.todoService.updateTodo(updateTodoRequest, todoId, userId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{todo-id}")
    public ResponseEntity<TodoResponse> findTodoById(
            @PathVariable("todo-id")
            final Long todoId
    ){
        return ResponseEntity.ok(this.todoService.findTodoById(todoId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TodoResponse>> findAllTodosByUserId(){
        final String userId = currentUserService.getUserId();
        return ResponseEntity.ok(this.todoService.findAllTodosForToday(userId));
    }

    @GetMapping("/category/{category-id}")
    public ResponseEntity<List<TodoResponse>> findAllTodosByCategory(
            @PathVariable("category-id")
            final Long categoryId
    ) {
        final String userId = currentUserService.getUserId();
        return ResponseEntity.ok(this.todoService.findAllTodosByCategory(categoryId, userId));
    }

    @GetMapping("/due")
    public ResponseEntity<List<TodoResponse>> findAllDueTodos(){
        final String userId = currentUserService.getUserId();
        return ResponseEntity.ok(this.todoService.findAllDueTodos(userId));
    }

    @DeleteMapping("/{todo-id}")
    public ResponseEntity<Void> deleteTodoById(
            @PathVariable("todo-id")
            final Long todoId
    ) {
        this.todoService.deleteTodoById(todoId);
        return ResponseEntity.ok().build();
    }
}
