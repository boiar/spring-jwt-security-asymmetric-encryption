package com.example.auth_security.service.category;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.exception.CategoryErrorCode;
import com.example.auth_security.category.exception.CategoryException;
import com.example.auth_security.category.mapper.CategoryMapper;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;
import com.example.auth_security.category.service.impl.CategoryServiceImpl;
import com.example.auth_security.common.exception.CommonErrorCode;
import com.example.auth_security.common.exception.CommonException;
import com.example.auth_security.stubs.category.CategoryRepositoryStub;
import com.example.auth_security.stubs.user.UserRepositoryStub;
import com.example.auth_security.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImplTest Unit Test")
class CategoryServiceImplTest {
    private final CategoryMapper categoryMapper = new CategoryMapper();


    //stubs
    private CategoryRepositoryStub categoryRepo;

    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private User testUser;

    private CreateCategoryRequest createCategoryRequest;
    private UpdateCategoryRequest updateCategoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void globalSetUp() {
        // init stubs
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        categoryRepo = new CategoryRepositoryStub();
        UserRepositoryStub userRepo = new UserRepositoryStub(encoder);

        // inject dependencies including the stub
        categoryService = new CategoryServiceImpl(categoryMapper, categoryRepo);

        // get users
        testUser = userRepo.getUserById(UserRepositoryStub.USER_1_ID);
        User anotherUser = userRepo.getUserById(UserRepositoryStub.USER_2_ID);

        // get categories
        testCategory = categoryRepo.getCategoryById(CategoryRepositoryStub.CAT_1_ID);
    }


    @Nested
    @DisplayName("Create Category Tests")
    class CreateTodoTests{

        @BeforeEach
        void nestedSetUp(){
            createCategoryRequest = CreateCategoryRequest.builder()
                    .name("New Category")
                    .description("New Description")
                    .build();
        }

        @Test
        @DisplayName("Should create category successfully when valid request")
        void shouldCreateTodoSuccessfully(){
            // ===== given =====
            String userId = testUser.getId();
            Long savedId = categoryService.createCategory(createCategoryRequest, userId);

            // ===== then =====
            Optional<Category> savedCategory = categoryRepo.findById(savedId);
            assertTrue(savedCategory.isPresent(), "Stub repo should contain saved todo");

            Category saved = savedCategory.get();


            assertEquals(createCategoryRequest.getName(), saved.getName(), "Name should match request");
            assertEquals(createCategoryRequest.getDescription(), saved.getDescription(), "Description should match request");

            assertEquals(userId, saved.getActorData().getCreatedBy(), "CreatedBy should be userId");
            assertNotNull(saved.getActorData(), "ActorData should be initialized");
            assertNotNull(saved.getTimingData(), "TimingData should be initialized");
        }

        @Test
        @DisplayName("Should throw Exception when category exists")
        void shouldThrowExceptionWhenCategoryExists(){
            // given
            String userId = testUser.getId();

            // create exists category
            createCategoryRequest.setName(testCategory.getName());
            createCategoryRequest.setDescription(testCategory.getDescription());


            CategoryException exception = assertThrows(
                    CategoryException.class,
                    () -> categoryService.createCategory(createCategoryRequest, userId)
            );

            // then
            assertNotNull(exception);
            assertEquals(CategoryErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER.getCode(), exception.getErrorCode(), "Should same error code");
        }
    }


   @Nested
    @DisplayName("Update Category Tests")
    class UpdateTodoTests{
        @BeforeEach
        void nestedSetUp(){
            updateCategoryRequest = UpdateCategoryRequest.builder()
                    .name("Updated Category")
                    .description("Updated Description")
                    .build();
        }

        @Test
        @DisplayName("Should update successfully a Todo when todo and category exist")
        void shouldSuccessfullyUpdateCategory(){
            String userId = testUser.getId();
            Long catId = testCategory.getId();
            // ===== when =====
            categoryService.updateCategory(updateCategoryRequest, catId, userId);

            // ===== then =====
            Optional<Category> updatedCategoryOpt = categoryRepo.findById(catId);
            assertTrue(updatedCategoryOpt.isPresent(), "Updated category should exist in repository");

            Category updatedTodo = updatedCategoryOpt.get();

            // Verify all fields were updated correctly
            assertEquals(updateCategoryRequest.getName(), updatedTodo.getName(), "Title should be updated");
            assertEquals(updateCategoryRequest.getDescription(), updatedTodo.getDescription(), "Description should be updated");
            assertEquals(userId, updatedTodo.getActorData().getLastModifiedBy(), "ActorData.updatedBy should be set to userId");
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when category not found")
        void shouldThrowExceptionWhenTodoNotFound(){
            String userId = testUser.getId();
            Long nonExistentCategoryId = 999L;

            CommonException exception = assertThrows(
                    CommonException.class,
                    () -> categoryService.updateCategory(updateCategoryRequest, nonExistentCategoryId, userId)
            );

            assertNotNull(exception);
            assertEquals(CommonErrorCode.YOU_NOT_HAVE_PERMISSION.getCode(), exception.getErrorCode());
            assertTrue(categoryRepo.findById(nonExistentCategoryId).isEmpty(), "No category should be saved");
        }
    }


    @Nested
    @DisplayName("Find Categories By User Id Tests")
    class FindAllByIdTests {
        @Test
        @DisplayName("Should return categories response")
        void shouldReturnCategoriesResponse() {

            List<CategoryResponse> response = categoryService.findAllByOwner(testUser.getId());

            assertAll("Response should have correct todo data",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(2, response.size(),  "Size should be 2")
            );
        }

        @Test
        @DisplayName("Should return empty when not found any categories")
        void shouldReturnEmptyWhenNotFoundAnyCategories() {
            categoryRepo.clear();
            List<CategoryResponse> response = categoryService.findAllByOwner(testUser.getId());

            assertAll("Response should have empty data",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(0, response.size(),  "Size should be 0")
            );
        }
    }

    @Nested
    @DisplayName("Find Category By Id Tests")
    class FindCategoryByIdTests {

        @Test
        @DisplayName("Should return category response when exists")
        void shouldReturnTodoResponse() {

            CategoryResponse response = categoryService.findByCategoryId(testCategory.getId());

            assertAll("Response should have correct category data",
                    () -> assertNotNull(response, "Response should not be null"),
                    () -> assertEquals(testCategory.getId(), response.getId(), "ID should match")
            );
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            Long notExistCategoryId = 999L;

            CategoryException exception = assertThrows(
                    CategoryException.class,
                    () -> categoryService.findByCategoryId(notExistCategoryId)
            );

            assertNotNull(exception);
            assertEquals(CategoryErrorCode.CATEGORY_NOT_EXISTS.getCode(), exception.getErrorCode());
            assertTrue(categoryRepo.findById(notExistCategoryId).isEmpty(), "Stub confirms todo doesn't exist");
        }

        @Test
        @DisplayName("Should handle null todo ID")
        void shouldHandleNullId() {
            CategoryException exception = assertThrows(
                    CategoryException.class,
                    () -> categoryService.findByCategoryId(null)
            );
            assertNotNull(exception);
            assertEquals(CategoryErrorCode.CATEGORY_NOT_EXISTS.getCode(), exception.getErrorCode());
        }

    }

    @Nested
    @DisplayName("Check Category For User Tests")
    class CheckCategoryForUserTests {
        @Test
        @DisplayName("Should return false when category not exists")
        void shouldReturnFalseWhenCategoryNotExists() {
            String newCategory = "New Category Name";
            assertDoesNotThrow(() -> categoryService.checkCategoryExistsForUser(newCategory, testUser.getId()));
        }

        @Test
        @DisplayName("Should throw exception when category exists")
        void shouldThrowExceptionWhenCategoryExists() {
           String existCategoryName = testCategory.getName();

            CategoryException exception = assertThrows(
                    CategoryException.class,
                    () -> categoryService.checkCategoryExistsForUser(existCategoryName, testUser.getId())
            );

            assertNotNull(exception);
            assertEquals(CategoryErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER.getCode(), exception.getErrorCode());
        }
    }


    @Nested
    @DisplayName("Check And Return Category For User Tests")
    class CheckAndReturnCategory {
        @Test
        @DisplayName("Should return category response when category not exists for user")
        void shouldReturnFalseWhenCategoryNotExists() {

            Category categoryObj = categoryService.checkAndReturnCategory(testCategory.getId(), testUser.getId());

            assertAll("Response should have correct category data",
                    () -> assertNotNull(categoryObj, "Category Object should not be null"),
                    () -> assertEquals(testCategory.getId(), categoryObj.getId(), "ID should match"),
                    () -> assertEquals(testCategory.getActorData().getCreatedBy(), categoryObj.getActorData().getCreatedBy(), "getActorData.createdBy should match user id")
            );
        }

        @Test
        @DisplayName("Should throw exception when category exists")
        void shouldThrowExceptionWhenCategoryExists() {
            String existCategoryName = testCategory.getName();

            CategoryException exception = assertThrows(
                    CategoryException.class,
                    () -> categoryService.checkCategoryExistsForUser(existCategoryName, testUser.getId())
            );

            assertNotNull(exception);
            assertEquals(CategoryErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER.getCode(), exception.getErrorCode());
        }
    }



   @Nested
    @DisplayName("Delete Category By Id Tests")
    class DeleteCategoryByIdTests{
        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategorySuccessfully(){
        }
    }
}