package com.example.auth_security.todo.mapper;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.todo.response.TodoResponse;
import org.apache.commons.lang3.StringUtils;
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
                   .timingData(new EntityAuditTimingData())
                   .actorData(new EntityAuditActorData())
                   .isDone(false)
                   .build();
    }

    public void mergerTodoEntity(final Todo todoToUpdate, final UpdateTodoRequest request) {
        todoToUpdate.setTitle(request.getTitle());
        todoToUpdate.setDescription(request.getDescription());
        todoToUpdate.setStartDate(request.getStartDate());
        todoToUpdate.setStartTime(request.getStartTime());
        todoToUpdate.setEndDate(request.getEndDate());
        todoToUpdate.setEndTime(request.getEndTime());
    }


    public TodoResponse toTodoResponse(final Todo todo) {

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(todo.getCategory().getId())
                .name(todo.getCategory().getName())
                .description(todo.getCategory().getDescription())
                .build();

        return TodoResponse.builder()
                           .id(todo.getId())
                           .userId(todo.getUser().getId())
                           .title(todo.getTitle())
                           .description(todo.getDescription())
                           .startDate(todo.getStartDate())
                           .startTime(todo.getStartTime())
                           .endDate(todo.getEndDate())
                           .endTime(todo.getEndTime())
                           .done(todo.isDone())
                           .category(categoryResponse)
                           .build();
                // TODO Category

    }
}
