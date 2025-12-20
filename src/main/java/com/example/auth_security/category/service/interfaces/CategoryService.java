package com.example.auth_security.category.service.interfaces;

import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    Long createCategory(CreateCategoryRequest request, String userId);

    void updateCategory(UpdateCategoryRequest request, Long catId, String userId);

    List<CategoryResponse> findAllByOwner(String userId);


    CategoryResponse findByCategoryId(Long catId);
    void deleteByCategoryId(Long catId);

}
