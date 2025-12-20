package com.example.auth_security.category.service.impl;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.exception.CategoryErrorCode;
import com.example.auth_security.category.exception.CategoryException;
import com.example.auth_security.category.mapper.CategoryMapper;
import com.example.auth_security.category.repository.CategoryRepo;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;
import com.example.auth_security.category.service.interfaces.CategoryService;
import com.example.auth_security.common.exception.CommonErrorCode;
import com.example.auth_security.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepo categoryRepo;
    @Override
    public Long createCategory(final CreateCategoryRequest request, final String userId) {
        checkCategoryExistsForUser(request.getName(), userId);
        final Category category = this.categoryMapper.toCategoryEntity(request);

        category.getActorData().setCreatedBy(userId);
        category.getActorData().setLastModifiedBy(userId);

        return this.categoryRepo.save(category).getId();
    }

    @Override
    public void updateCategory(final UpdateCategoryRequest request, final Long catId, final String userId) {
        Category categoryWillUpdate = categoryRepo.findById(catId)
                                                .orElseThrow(() ->
                                                        new CategoryException(CategoryErrorCode.CATEGORY_NOT_EXISTS)
                                                );

        if (!Objects.equals(categoryWillUpdate.getActorData().getCreatedBy(), userId)) {
            throw new CommonException(CommonErrorCode.YOU_NOT_HAVE_PERMISSION);
        }

        this.categoryMapper.mergeCategoryEntity(categoryWillUpdate, request);
        this.categoryRepo.save(categoryWillUpdate);
    }

    @Override
    public List<CategoryResponse> findAllByOwner(String userId) {
        return this.categoryRepo.findAllCategoriesByUserId(userId)
                                .stream()
                                .map(this.categoryMapper::toCategoryResponse)
                                .toList();
    }

    @Override
    public CategoryResponse findByCategoryId(Long catId) {
        return categoryRepo.findById(catId)
                           .map(this.categoryMapper::toCategoryResponse)
                           .orElseThrow(() ->
                                new CategoryException(CategoryErrorCode.CATEGORY_NOT_EXISTS)
                           );
    }

    @Override
    public void deleteByCategoryId(Long catId) {
        // TODO
        // mark the category for deletion
        // the scheduler should pick up all the marked categories and perform the deletion
    }

    private void checkCategoryExistsForUser(final String catName, final String userId) {
        final boolean alreadyExistsForUser = this.categoryRepo.findByNameAndUserId(catName, userId) > 0;
        if (alreadyExistsForUser) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER);
        }
    }

}
