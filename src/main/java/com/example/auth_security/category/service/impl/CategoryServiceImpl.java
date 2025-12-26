package com.example.auth_security.category.service.impl;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.exception.CategoryErrorCode;
import com.example.auth_security.category.exception.CategoryException;
import com.example.auth_security.category.mapper.CategoryMapper;
import com.example.auth_security.category.repository.CategoryRepository;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;
import com.example.auth_security.category.service.interfaces.CategoryService;
import com.example.auth_security.common.exception.CommonErrorCode;
import com.example.auth_security.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepo;
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
        Category categoryWillUpdate = categoryRepo.findByIdForUser(catId, userId)
                                                .orElseThrow(() ->
                                                        new CommonException(CommonErrorCode.YOU_NOT_HAVE_PERMISSION)
                                                );

        this.categoryMapper.mergeCategoryEntity(categoryWillUpdate, request);
        this.categoryRepo.save(categoryWillUpdate);
    }

    @Override
    public List<CategoryResponse> findAllByOwner(String userId) {
        return this.categoryRepo.findAllForUser(userId)
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


    private void checkCategoryExistsForUser(final String catName, final String userId) {
        final boolean alreadyExistsForUser = this.categoryRepo.existsByNameForUser(catName, userId);
        if (alreadyExistsForUser) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER);
        }
    }

    @Override
    public void deleteByCategoryId(Long catId) {
        // TODO
        // mark the category for deletion
        // the scheduler should pick up all the marked categories and perform the deletion
    }

}
