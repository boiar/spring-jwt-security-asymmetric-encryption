package com.example.auth_security.category.mapper;

import ch.qos.logback.core.util.StringUtil;
import com.example.auth_security.category.entity.Category;
import com.example.auth_security.category.request.CreateCategoryRequest;
import com.example.auth_security.category.request.UpdateCategoryRequest;
import com.example.auth_security.category.response.CategoryResponse;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public Category toCategoryEntity(final CreateCategoryRequest req) {
        return Category.builder()
                       .name(req.getName())
                       .description(req.getDescription())
                       .timingData(new EntityAuditTimingData())
                       .actorData(new EntityAuditActorData())
                       .build();
    }

    public CategoryResponse toCategoryResponse(final Category category) {
        return CategoryResponse.builder()
                               .id(category.getId())
                               .name(category.getName())
                               .description(category.getDescription())
                               .createdDate(category.getTimingData().getCreatedDate())
                               .updatedData(category.getTimingData().getUpdatedDate())
                               .todosCount(category.getTodos() != null ? category.getTodos().size() : 0)
                               .build();
    }


    public void mergeCategoryEntity(final Category categoryWillUpdate, final UpdateCategoryRequest req) {
        categoryWillUpdate.setName(req.getName());
        categoryWillUpdate.setDescription(req.getDescription());
    }


}
