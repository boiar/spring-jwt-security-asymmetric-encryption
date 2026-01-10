package com.example.auth_security.stubs.todo;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.repository.TodoRepository;
import com.example.auth_security.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class TodoRepositoryStub implements TodoRepository {

    private final Map<Long, Todo> store = new HashMap<>();
    private long currentId = 3L;
    public static final Long TODO_1_ID = 1L;
    public static final Long TODO_2_ID = 2L;




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


    public Todo createFirstTodo(User user, Category category){
        // Initialize ActorData
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(user.getId());
        actorData.setLastModifiedBy(user.getId());

        // Initialize TimingData
        EntityAuditTimingData timingData = new EntityAuditTimingData();
        timingData.setCreatedDate(LocalDateTime.now());
        timingData.setUpdatedDate(LocalDateTime.now());

        Todo testTodo = Todo.builder()
                .id(TODO_1_ID)
                .title("Test Todo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isDone(false)
                .user(user)
                .category(category)
                .actorData(actorData)
                .timingData(timingData)
                .build();

        store.put(testTodo.getId(), testTodo);
        return testTodo;
    }

    public Todo createSecondTodo(User user, Category category){
        // Initialize ActorData
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(user.getId());
        actorData.setLastModifiedBy(user.getId());

        // Initialize TimingData
        EntityAuditTimingData timingData = new EntityAuditTimingData();
        timingData.setCreatedDate(LocalDateTime.now());
        timingData.setUpdatedDate(LocalDateTime.now());

        Todo testTodo = Todo.builder()
                .id(TODO_2_ID)
                .title("Another Test Todo")
                .description("Another Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isDone(false)
                .user(user)
                .category(category)
                .actorData(actorData)
                .timingData(timingData)
                .build();

        store.put(testTodo.getId(), testTodo);
        return testTodo;
    }

    public void clear() {
        store.clear();
    }

    public int count() {
        return store.size();
    }

}
