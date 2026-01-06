package com.example.auth_security.category.integration;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.core.security.JwtService;
import com.example.auth_security.todo.entity.Todo;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private Category testCategory;
    private Category anotherTestCategory;
    private String accessToken;

    @BeforeEach
    void setUp(){

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


        userRepo.save(testUser);
        this.accessToken = this.jwtService.generateAccessToken(testUser.getUsername());

    }


    @Test
    @DisplayName("Create Category Endpoint V1 Tests")
    void shouldCreateCategory() throws Exception {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("category test");
        request.setDescription("category test desc");




        // When & Then
        MvcResult result =mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Long catId = objectMapper
                .readTree(responseBody)
                .get("id")
                .asLong();


        //Verify in database
        Category savedCategory = categoryRepo.findById(catId)
                .orElseThrow(() -> new AssertionError("Category not found"));

        assertThat(savedCategory.getName()).isEqualTo("category test");
        assertThat(savedCategory.getDescription()).isEqualTo("category test desc");
    }




   @Test
    @DisplayName("Update Category Endpoint V1 Tests")
    void shouldUpdateTodo() throws Exception {

        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testCategory = Category.builder()
            .name("category")
            .description("desc")
            .actorData(actorData)
            .timingData(new EntityAuditTimingData())
            .build();

        this.testCategory = categoryRepo.save(testCategory);

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Updated category");
        request.setDescription("Updated desc");


        // When & Then
        mockMvc.perform(put("/api/v1/categories/" + testCategory.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Category updated = categoryRepo.findById(testCategory.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated category");
        assertThat(updated.getDescription()).isEqualTo("Updated desc");

    }



    @Test
    @DisplayName("Find Category By Id Endpoint V1 Tests")
    void shouldFindCategoryById() throws Exception {
        // Given
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testCategory = Category.builder()
                .name("category")
                .description("desc")
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .build();

        this.testCategory = categoryRepo.save(testCategory);


        // When & Then
        mockMvc.perform(get("/api/v1/categories/" + testCategory.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.name").value("category"));
    }

    @Test
    @DisplayName("Find All Categories Endpoint V1 Tests")
    void shouldFindAllCategories() throws Exception {
        // Given
        EntityAuditActorData actorData = new EntityAuditActorData();
        actorData.setCreatedBy(testUser.getId());

        this.testCategory = Category.builder()
                .name("category")
                .description("desc")
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .build();

        this.anotherTestCategory = Category.builder()
                .name("another category")
                .description("desc")
                .actorData(actorData)
                .timingData(new EntityAuditTimingData())
                .build();

        this.testCategory = categoryRepo.save(testCategory);
        this.anotherTestCategory = categoryRepo.save(anotherTestCategory);


        // When & Then
        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }




    @Test
    @DisplayName("Delete Category Endpoint V1 Tests")
    void shouldDeleteCategory() throws Exception {
        // Given
       EntityAuditActorData actorData = new EntityAuditActorData();
       actorData.setCreatedBy(testUser.getId());

       this.testCategory = Category.builder()
               .name("category")
               .description("desc")
               .actorData(actorData)
               .timingData(new EntityAuditTimingData())
               .build();


       this.testCategory = categoryRepo.save(testCategory);

        // When & Then
        mockMvc.perform(delete("/api/v1/categories/" + testCategory.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify deleted from database
        //assertThat(categoryRepo.findById(testCategory.getId())).isEmpty();

    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/categories")).andExpect(status().isUnauthorized());
    }


}
