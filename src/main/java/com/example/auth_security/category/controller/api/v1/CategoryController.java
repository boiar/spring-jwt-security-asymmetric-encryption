package com.example.auth_security.category.controller.api.v1;

import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;
import com.example.auth_security.category.service.CategoryService;
import com.example.auth_security.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category API V1")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> findCategoryById(
            @PathVariable("category-id")
            final Long catId
    ){
        return ResponseEntity.ok(this.categoryService.findByCategoryId(catId));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> allCategories(
            final Authentication authentication
    ){
        final String userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(this.categoryService.findAllByOwner(userId));
    }


    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody
            @Valid
            final CreateCategoryRequest req,
            final Authentication authentication
    ){
        final String userId = ((User) authentication.getPrincipal()).getId();

        final Long catId = this.categoryService.createCategory(req, userId);
        CategoryResponse response = this.categoryService.findByCategoryId(catId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable("category-id")
            final Long catId,
            @RequestBody
            @Valid
            final UpdateCategoryRequest req,

            final Authentication authentication
    ){
        final String userId = ((User) authentication.getPrincipal()).getId();

        this.categoryService.updateCategory(req, catId, userId);
        CategoryResponse response = this.categoryService.findByCategoryId(catId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategoryById(
            @PathVariable("category-id")
            final Long catId
    ){
        this.categoryService.deleteByCategoryId(catId);
         return ResponseEntity.ok().build();
    }


}
