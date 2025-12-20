package com.example.auth_security.todo.controller.api.v1;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Tag(name= "Todos", description = "Todo API v1")
public class TodoController {



}
