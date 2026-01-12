package com.example.auth_security.service.todo;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.stubs.category.CategoryRepositoryStub;
import com.example.auth_security.common.exception.CommonErrorCode;
import com.example.auth_security.common.exception.CommonException;
import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.exception.TodoErrorCode;
import com.example.auth_security.todo.exception.TodoException;
import com.example.auth_security.todo.mapper.TodoMapper;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.todo.response.TodoResponse;
import com.example.auth_security.todo.service.impl.TodoServiceImpl;
import com.example.auth_security.stubs.todo.TodoRepositoryStub;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.service.UserService;
import com.example.auth_security.stubs.user.UserRepositoryStub;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImplTest Unit Test")
class TodoServiceImplTest {
    private final TodoMapper todoMapper = new TodoMapper();

    @Mock
    private UserService userService;
    //stubs
    private TodoRepositoryStub todoRepo;
    private CategoryRepositoryStub categoryRepo;

    private TodoServiceImpl todoService;

    private Category testCategory;
    private Category anotherCategory;
    private Todo testTodo;
    private Todo anotherTestTodo;
    private User testUser;

    private CreateTodoRequest todoCreateRequest;
    private UpdateTodoRequest todoUpdateRequest;

    @BeforeEach
    void globalSetUp() {
        // init stubs
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        todoRepo = new TodoRepositoryStub();
        categoryRepo = new CategoryRepositoryStub();
        UserRepositoryStub userRepo = new UserRepositoryStub(encoder);

        // inject dependencies including the stub
        todoService = new TodoServiceImpl(todoMapper, todoRepo , categoryRepo, userService, userRepo);

        // get users
        testUser = userRepo.getUserById(UserRepositoryStub.USER_1_ID);
        User anotherUser = userRepo.getUserById(UserRepositoryStub.USER_2_ID);

        // get categories
        testCategory = categoryRepo.getCategoryById(CategoryRepositoryStub.CAT_1_ID);
        anotherCategory = categoryRepo.getCategoryById(CategoryRepositoryStub.CAT_2_ID);

        // get todos
        testTodo = todoRepo.createFirstTodo(testUser, testCategory);
        anotherTestTodo = todoRepo.createSecondTodo(testUser, testCategory);

        TodoResponse todoResponse = TodoResponse.builder()
                .id(1L)
                .title("Test todo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .done(false)
                .build();
    }


    @Nested
    @DisplayName("Create Todo Tests")
    class CreateTodoTests{

        @BeforeEach
        void nestedSetUp(){
            todoCreateRequest = CreateTodoRequest.builder()
                    .title("New Todo")
                    .description("New Description")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(1))
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(18, 0))
                    .categoryId(1L)
                    .build();
        }

        @Test
        @DisplayName("Should create todo successfully when valid and valid request and category exists")
        void shouldCreateTodoSuccessfully(){
            // ===== given =====
            String userId = testUser.getId();
            Long savedId = todoService.createTodo(todoCreateRequest, userId);

            // ===== then =====
            Optional<Todo> savedTodo = todoRepo.findById(savedId);
            assertTrue(savedTodo.isPresent(), "Stub repo should contain saved todo");

            Todo saved = savedTodo.get();


            assertEquals(todoCreateRequest.getTitle(), saved.getTitle(), "Title should match request");
            assertEquals(todoCreateRequest.getDescription(), saved.getDescription(), "Description should match request");
            assertEquals(todoCreateRequest.getStartDate(), saved.getStartDate(), "Start date should match request");
            assertEquals(todoCreateRequest.getEndDate(), saved.getEndDate(), "End date should match request");
            assertEquals(todoCreateRequest.getStartTime(), saved.getStartTime(), "Start time should match request");
            assertEquals(todoCreateRequest.getEndTime(), saved.getEndTime(), "End time should match request");
            assertEquals(testCategory, saved.getCategory(), "Category should be set");
            assertEquals(testUser, saved.getUser(), "User should be set");
            assertEquals(userId, saved.getActorData().getCreatedBy(), "CreatedBy should be userId");
            assertFalse(saved.isDone(), "New todo should not be done");
            assertNotNull(saved.getActorData(), "ActorData should be initialized");
            assertNotNull(saved.getTimingData(), "TimingData should be initialized");
        }

        @Test
        @DisplayName("Should throw Exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound(){
            // given
            String userId = testUser.getId();
            categoryRepo.clear();
            todoRepo.clear();


            CommonException exception = assertThrows(
                    CommonException.class,
                    () -> todoService.createTodo(todoCreateRequest, userId)
            );

            // then
            assertNotNull(exception);
            assertEquals(CommonErrorCode.YOU_NOT_HAVE_PERMISSION.getCode(), exception.getErrorCode());
            verifyNoInteractions(userService);
            assertTrue(todoRepo.findAllTodayTodosByUserId(userId).isEmpty(),
                    "No todo should be saved when category validation fails");
        }

        @Test
        @DisplayName("Should Handle null categoryId in request")
        void shouldHandleNullCatIdInRequest() {
            // given
            String userId = testUser.getId();
            categoryRepo.clear();
            todoRepo.clear();
            todoCreateRequest.setCategoryId(null);

            CommonException exception = assertThrows(
                    CommonException.class,
                    () -> todoService.createTodo(todoCreateRequest, userId)
            );

            assertNotNull(exception);
            assertEquals(CommonErrorCode.YOU_NOT_HAVE_PERMISSION.getCode(), exception.getErrorCode());
            verifyNoInteractions(userService);
            assertTrue(todoRepo.findAllTodayTodosByUserId(userId).isEmpty(),
                    "No todo should be saved when category validation fails");

        }

    }


    @Nested
    @DisplayName("Update Todo Tests")
    class UpdateTodoTests{
        @BeforeEach
        void nestedSetUp(){
            todoUpdateRequest = UpdateTodoRequest.builder()
                    .title("Updated Todo")
                    .description("Updated Description")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(2))
                    .startTime(LocalTime.of(11, 0))
                    .endTime(LocalTime.of(19, 0))
                    .categoryId(1L)
                    .build();
        }

        @Test
        @DisplayName("Should update successfully a Todo when todo and category exist")
        void shouldSuccessfullyUpdateTodo(){
            String userId = testUser.getId();
            Long todoId = testTodo.getId();
            // ===== when =====
            todoService.updateTodo(todoUpdateRequest, todoId, userId);

            // ===== then =====
            Optional<Todo> updatedTodoOpt = todoRepo.findById(todoId);
            assertTrue(updatedTodoOpt.isPresent(), "Updated todo should exist in repository");

            Todo updatedTodo = updatedTodoOpt.get();

            // Verify all fields were updated correctly
            assertEquals(todoUpdateRequest.getTitle(), updatedTodo.getTitle(), "Title should be updated");
            assertEquals(todoUpdateRequest.getDescription(), updatedTodo.getDescription(), "Description should be updated");
            assertEquals(todoUpdateRequest.getStartDate(), updatedTodo.getStartDate(), "Start date should be updated");
            assertEquals(todoUpdateRequest.getEndDate(), updatedTodo.getEndDate(), "End date should be updated");
            assertEquals(todoUpdateRequest.getStartTime(), updatedTodo.getStartTime(), "Start time should be updated");
            assertEquals(todoUpdateRequest.getEndTime(), updatedTodo.getEndTime(), "End time should be updated");
            assertEquals(testCategory, updatedTodo.getCategory(), "Category should be updated");
            assertEquals(userId, updatedTodo.getActorData().getLastModifiedBy(), "ActorData.updatedBy should be set to userId");
            assertNotNull(updatedTodo.getTimingData().getUpdatedDate(), "ActorData.updatedAt should be set");
        }

        @Test
        @DisplayName("Should throw TodoNotFoundException when Todo not found")
        void shouldThrowExceptionWhenTodoNotFound(){
            String userId = testUser.getId();
            Long nonExistentTodoId = 999L;

            TodoException exception = assertThrows(
                    TodoException.class,
                    () -> todoService.updateTodo(todoUpdateRequest, nonExistentTodoId, userId)
            );

            assertNotNull(exception);
            assertEquals(TodoErrorCode.TODO_NOT_EXISTS.getCode(), exception.getErrorCode());
            assertTrue(todoRepo.findById(nonExistentTodoId).isEmpty(),
                    "No todo should be saved when category validation fails");
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Should throw Exception when Category not found")
        void shouldThrowExceptionWhenCategoryNotFound(){
            String userId = testUser.getId();
            Long nonExistentCategoryId = 999L;

            // Save the todo
            Todo savedTodo = todoRepo.save(testTodo);
            Long todoId = savedTodo.getId();

            todoUpdateRequest.setCategoryId(nonExistentCategoryId);


            CommonException exception = assertThrows(
                    CommonException.class,
                    () -> todoService.updateTodo(todoUpdateRequest, todoId, userId)
            );

            assertNotNull(exception);
            assertEquals(CommonErrorCode.YOU_NOT_HAVE_PERMISSION.getCode(), exception.getErrorCode());
            assertTrue(categoryRepo.findById(nonExistentCategoryId).isEmpty(), "No todo should be saved when category validation fails");
            verifyNoInteractions(userService);
        }


    }


    @Nested
    @DisplayName("Find Todo By Id Tests")
    class FindTodoByIdTests {

        @Test
        @DisplayName("Should return todo response when todo exists")
        void shouldReturnTodoResponse() {

            // Save the todo
            Todo savedTodo = todoRepo.save(testTodo);
            Long todoId = savedTodo.getId();
            TodoResponse response = todoService.findTodoById(todoId);

            assertAll("Response should have correct todo data",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(todoId, response.getId(), "ID should match")
            );
        }

        @Test
        @DisplayName("Should throw exception when todo not found")
        void shouldThrowExceptionWhenTodoNotFound() {
            Long notExistTodoId = 999L;

            TodoException exception = assertThrows(
                    TodoException.class,
                    () -> todoService.findTodoById(notExistTodoId)
            );

            assertNotNull(exception);
            assertEquals(TodoErrorCode.TODO_NOT_EXISTS.getCode(), exception.getErrorCode());
            assertTrue(todoRepo.findById(notExistTodoId).isEmpty(), "Stub confirms todo doesn't exist");
        }

        @Test
        @DisplayName("Should handle null todo ID")
        void shouldHandleNullId() {

            TodoException exception = assertThrows(
                    TodoException.class,
                    () -> todoService.findTodoById(null)
            );
            assertNotNull(exception);
            assertEquals(TodoErrorCode.TODO_NOT_EXISTS.getCode(), exception.getErrorCode());
            assertTrue(todoRepo.findById(null).isEmpty(), "Stub confirms todo doesn't exist");
        }

    }

    @Nested
    @DisplayName("Find Today Todos For User Tests")
    class FindTodosForTodayByUserIdTests {
        @Test
        @DisplayName("Should return todos response")
        void shouldReturnTodosScheduledForToday() {
            // Save today todo
            LocalDate today = LocalDate.now();
            testTodo.setStartDate(today);
            Todo savedTodo = todoRepo.save(testTodo);
            String userId = savedTodo.getUser().getId();
            List<TodoResponse> response = todoService.findAllTodosForToday(userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(savedTodo.getId(), response.get(0).getId()),
                    () -> assertEquals(today, response.get(0).getStartDate()),
                    () -> assertEquals(userId, response.get(0).getUserId())
            );
        }

        @Test
        @DisplayName("Should return only today's todos for the user")
        void shouldReturnTodosOnlyTodayResponse() {
            // Save today todo
            LocalDate today = LocalDate.now();
            testTodo.setStartDate(today);
            Todo savedTodo = todoRepo.save(testTodo);

            // Save a past todo (7 days ago) - should NOT be returned
            anotherTestTodo.setStartDate(today.minusDays(7));
            todoRepo.save(anotherTestTodo);

            String userId = testUser.getId();

            List<TodoResponse> response = todoService.findAllTodosForToday(userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(1, response.size(),  "Size should be 1"),
                    () -> assertEquals(savedTodo.getId(), response.get(0).getId())
            );
        }

        @Test
        @DisplayName("Should return empty today's todos array for the user")
        void shouldReturnEmptyTodosTodayResponse() {
            LocalDate today = LocalDate.now();
            // Save a past todo (7 days ago)
            anotherTestTodo.setStartDate(today.minusDays(7));
            todoRepo.save(anotherTestTodo);

            testTodo.setStartDate(today.minusDays(7));
            todoRepo.save(testTodo);

            String userId = testUser.getId();

            List<TodoResponse> response = todoService.findAllTodosForToday(userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertTrue(response.isEmpty(), "Response should be empty"),
                    () -> assertEquals(0, response.size(),  "Size should be 0")
            );
        }

    }


    @Nested
    @DisplayName("Find Todos By Category For User Tests")
    class FindTodosByCategoryAndUserIdTests {
        @Test
        @DisplayName("Should return todos by category response")
        void shouldReturnTodosByCategoryForUser() {
            // Save todo
            String userId = testTodo.getUser().getId();
            Long catId = testTodo.getCategory().getId();

            // make another todo with another category
            anotherTestTodo.setCategory(anotherCategory);
            todoRepo.save(anotherTestTodo);

            List<TodoResponse> response = todoService.findAllTodosByCategory(catId, userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(1, response.size(),  "Size should be 1"),
                    () -> assertEquals(testTodo.getId(), response.get(0).getId(), ""),
                    () -> assertEquals(catId, response.get(0).getCategory().getId(), "Should the same category"),
                    () -> assertEquals(userId, response.get(0).getUserId())
            );
        }

        @Test
        @DisplayName("Should return only today's todos for the user")
        void shouldReturnOnlyTodosByCategoryResponse() {
            // Save todo with category
            LocalDate today = LocalDate.now();
            testTodo.setStartDate(today);
            Todo savedTodo = todoRepo.save(testTodo);

            // Save todo with another category
            anotherTestTodo.setCategory(anotherCategory);
            todoRepo.save(anotherTestTodo);

            String userId = testUser.getId();
            Long catId = savedTodo.getCategory().getId();

            List<TodoResponse> response = todoService.findAllTodosByCategory(catId, userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(1, response.size(),  "Size should be 1"),
                    () -> assertEquals(savedTodo.getCategory().getId(), response.get(0).getCategory().getId())
            );
        }

    }



    @Nested
    @DisplayName("Find Due Todos For User Tests")
    class FindDueTodosForByUserIdTests {
        @Test
        @DisplayName("Should return due todos response")
        void shouldReturnTodosScheduledForToday() {
            // Save tomorrow todo
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDate yesterday = LocalDate.now().minusDays(2);
            LocalTime endTime = LocalTime.now().plusHours(2);

            testTodo.setEndDate(tomorrow);
            testTodo.setEndTime(endTime);
            Todo savedTodo = todoRepo.save(testTodo);

            // make another todo is in the past
            anotherTestTodo.setEndDate(yesterday);
            anotherTestTodo.setEndTime(LocalTime.of(0,0));
            todoRepo.save(anotherTestTodo);

            String userId  = savedTodo.getUser().getId();

            List<TodoResponse> response = todoService.findAllDueTodos(userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(tomorrow, response.get(0).getEndDate()),
                    () -> assertEquals(endTime, response.get(0).getEndTime()),
                    () -> assertEquals(1, response.size(),  "Size should be 1"),
                    () -> assertEquals(userId, response.get(0).getUserId())
            );
        }

        @Test
        @DisplayName("Should return only due todos for the user")
        void shouldReturnTodosOnlyTodayResponse() {
            // Save tomorrow todo
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalTime endTime = LocalTime.now().plusHours(2);

            testTodo.setEndDate(tomorrow);
            testTodo.setEndTime(endTime);
            Todo savedTodo = todoRepo.save(testTodo);

            anotherTestTodo.setEndDate(yesterday);
            todoRepo.save(anotherTestTodo);
            String userId  = savedTodo.getUser().getId();

            List<TodoResponse> response = todoService.findAllDueTodos(userId);

            assertAll("Validate returned todos",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(tomorrow, response.get(0).getEndDate()),
                    () -> assertEquals(endTime, response.get(0).getEndTime()),
                    () -> assertEquals(1, response.size(),  "Size should be 1"),
                    () -> assertEquals(userId, response.get(0).getUserId())
            );
        }

    }



    @Nested
    @DisplayName("Delete Todo By Id Tests")
    class DeleteTodoByIdTests{

        @Test
        @DisplayName("Should delete todo successfully")
        void shouldDeleteTodoSuccessfully(){
            Todo savedTodo = todoRepo.save(testTodo);
            Long todoId = savedTodo.getId();
            todoService.deleteTodoById(todoId);
            assertTrue(todoRepo.findById(todoId).isEmpty(),  "Stub confirms todo doesn't exist");
        }
    }






}