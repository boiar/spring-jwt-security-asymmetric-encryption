package com.example.auth_security.todo.stubs;

import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.repository.TodoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TodoRepositoryStub implements TodoRepository {

    private final Map<Long, Todo> store = new HashMap<>();
    private long currentId = 1L;

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(currentId++);
        }
        store.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long todoId) {
        return Optional.ofNullable(store.get(todoId));
    }

    @Override
    public List<Todo> findAllTodayTodosByUserId(String userId) {
        LocalDate today = LocalDate.now();
        List<Todo> result = new ArrayList<>();

        for (Todo todo : store.values()) {
            if (todo.getUser() != null
                    && userId.equals(todo.getUser().getId())
                    && todo.getStartDate() != null
                    && todo.getStartDate().equals(today)) {
                        result.add(todo);
                    }
        }
        return result;
    }

    @Override
    public List<Todo> findAllByUserIdAndCategoryId(String userId, Long categoryId) {
        List<Todo> result = new ArrayList<>();
        for (Todo todo : store.values()) {
            if (todo.getUser() != null && userId.equals(todo.getUser().getId())
                    && todo.getCategory() != null && categoryId.equals(todo.getCategory().getId())) {
                result.add(todo);
            }
        }
        return result;
    }

    @Override
    public List<Todo> findAllDueTodos(String userId) {
        List<Todo> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (Todo todo : store.values()) {
            if (todo.getUser() != null
                && userId.equals(todo.getUser().getId())
                && todo.getEndDate() != null
                && todo.getEndTime() != null
                && !todo.getEndDate().isBefore(today)  // endDate >= today
                && !todo.getEndTime().isBefore(now)) {  // endTime >= now
                    result.add(todo);
                }
        }
        return result;
    }

    @Override
    public void deleteById(Long todoId) {
        store.remove(todoId);
    }
}
