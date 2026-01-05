package com.example.auth_security.todo.integration;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.core.security.JwtService;
import com.example.auth_security.todo.entity.Todo;
import com.example.auth_security.todo.repository.TodoRepository;
import com.example.auth_security.todo.request.CreateTodoRequest;
import com.example.auth_security.todo.request.UpdateTodoRequest;
import com.example.auth_security.user.entity.User;
import com.example.auth_security.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private JwtService jwtService;

    private User testUser;

    private Todo testTodo;
    private Todo anotherTestTodo;

    private Category testCategory;

    private String accessToken;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;



    @BeforeEach
    void setUp(){

        this.startDate = LocalDate.now();
        this.endDate   = LocalDate.now().plusDays(7);
        this.startTime = LocalTime.now().plusHours(3);
        this.endTime   = LocalTime.now().plusHours(7);



        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy("user");

        this.testUser = User.builder()
                .firstName("Test")
                .lastName("test")
                .email("test@example.com")
                .phoneNumber("0123456789")
                .password("pass")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .enabled(true)
                .timingData(new EntityAuditTimingData())
                .actorData(actorData)
                .build();

        EntityAuditActorData actorDataCategory = new EntityAuditActorData();
        actorDataCategory.setCreatedBy("APP");

        this.testCategory = Category.builder()
                .name("Work")
                .description("Work related todos")
                .timingData(new EntityAuditTimingData())
                .actorData(actorDataCategory)
                .build();

        userRepo.save(testUser);
        testCategory = categoryRepo.save(testCategory);

        this.accessToken = this.jwtService.generateAccessToken(testUser.getUsername());

    }


    @Test
    @DisplayName("Create Todo Endpoint V1 Tests")
    void shouldCreateTodo() throws Exception {
        // Given
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("work");
        request.setDescription("work test");
        request.setStartDate(startDate);
        request.setStartTime(startTime);
        request.setEndDate(endDate);
        request.setEndTime(endTime);
        request.setCategoryId(testCategory.getId());


        // When & Then
        MvcResult result =mockMvc.perform(post("/api/v1/todos")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Long todoId = objectMapper
                .readTree(responseBody)
                .get("id")
                .asLong();


        //Verify in database
        Todo savedTodo = todoRepo.findById(todoId)
                .orElseThrow(() -> new AssertionError("Todo not found"));

        assertThat(savedTodo.getTitle()).isEqualTo("work");
        assertThat(savedTodo.getDescription()).isEqualTo("work test");
    }


    @Test
    @DisplayName("Update Todo Endpoint V1 Tests")
    void shouldUpdateTodo() throws Exception {

        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testTodo = Todo.builder()
            .title("Updated title")
            .description("Updated desc")
            .startDate(startDate)
            .startTime(startTime)
            .endDate(endDate)
            .endTime(endTime)
            .user(testUser)
            .actorData(actorData)
            .timingData(new EntityAuditTimingData())
            .category(testCategory)
            .build();

            this.testTodo = todoRepo.save(testTodo);

        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated desc");
        request.setStartDate(startDate);
        request.setStartTime(startTime);
        request.setEndDate(endDate);
        request.setEndTime(endTime);
        request.setCategoryId(testCategory.getId());


        // When & Then
        MvcResult result =  mockMvc.perform(put("/api/v1/todos/" + testTodo.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andReturn();

        Todo updated = todoRepo.findById(testTodo.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getDescription()).isEqualTo("Updated desc");

    }

    @Test
    @DisplayName("Find Todo Endpoint V1 Tests")
    void shouldFindTodo() throws Exception {
        // Given
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testTodo = Todo.builder()
                .title("Test Todo")
                .description("Test Todo desc")
                .startDate(startDate)
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .user(testUser)
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .category(testCategory)
                .build();

        this.testTodo = todoRepo.save(testTodo);


        // When & Then
        mockMvc.perform(get("/api/v1/todos/" + testTodo.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId()))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }


    @Test
    @DisplayName("Find Today's Todo Endpoint V1 Tests")
    void shouldFindAllTodosForToday() throws Exception {
        // Given
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testTodo = Todo.builder()
                .title("Test Todo")
                .description("Test Todo desc")
                .startDate(startDate)
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .user(testUser)
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .category(testCategory)
                .build();

        this.anotherTestTodo = Todo.builder()
                .title("Another Test Todo")
                .description("Another Test Todo desc")
                .startDate(startDate.plusDays(1))
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .user(testUser)
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .category(testCategory)
                .build();


        this.testTodo = todoRepo.save(testTodo);
        this.anotherTestTodo = todoRepo.save(anotherTestTodo);


        // When & Then
        mockMvc.perform(get("/api/v1/todos/today")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Delete Todo Endpoint V1 Tests")
    void shouldDeleteTodo() throws Exception {
        // Given
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testTodo = Todo.builder()
                .title("Test Todo")
                .description("Test Todo desc")
                .startDate(startDate)
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .user(testUser)
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .category(testCategory)
                .build();

        this.testTodo = todoRepo.save(testTodo);


        // When & Then
        mockMvc.perform(delete("/api/v1/todos/" + testTodo.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify deleted from database
        assertThat(todoRepo.findById(testTodo.getId())).isEmpty();

    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/todos/today")).andExpect(status().isUnauthorized());
    }



}
