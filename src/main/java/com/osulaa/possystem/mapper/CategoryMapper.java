package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.category.CategoryRequest;
import com.osulaa.possystem.dto.category.CategoryResponse;
import com.osulaa.possystem.entity.Category;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntity(Category category, CategoryRequest request) {

        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }
}
