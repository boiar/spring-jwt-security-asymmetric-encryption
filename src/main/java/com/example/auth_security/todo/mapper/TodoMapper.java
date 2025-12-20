package com.example.auth_security.todo.mapper;

import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.response.TodoResponse;
import org.springframework.stereotype.Service;

@Service
public class TodoMapper {

    public Todo toTodoEntity(final CreateTodoRequest req) {
        return Todo.builder()
                   .title(req.getTitle())
                   .description(req.getDescription())
                   .startDate(req.getStartDate())
                   .endDate(req.getEndDate())
                   .startTime(req.getStartTime())
                   .endTime(req.getEndTime())
                   .isDone(false)
                   .build();
    }


    public TodoResponse toTodoResponse(final Todo todo) {
        return TodoResponse.builder()
                           .id(todo.getId())
                           .title(todo.getTitle())
                           .description(todo.getDescription())
                           .startDate(todo.getStartDate())
                           .endTime(todo.getEndTime())
                           .done(todo.isDone())
                           .build();
                // TODO Category

    }
}
