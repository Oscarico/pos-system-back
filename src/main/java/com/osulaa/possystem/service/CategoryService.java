package com.osulaa.possystem.service;

import com.osulaa.possystem.mapper.PageMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.category.CategoryRequest;
import com.osulaa.possystem.dto.category.CategoryResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.entity.Category;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.CategoryMapper;
import com.osulaa.possystem.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PageMapper pageMapper;

    public PageResponse<CategoryResponse> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categories;

        if (search == null || search.isBlank()) {
            categories = categoryRepository.findByActiveTrue(pageable);
        } else {
            categories = categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(search, pageable);
        }

        return pageMapper.toPageResponse(categories, categoryMapper::toResponse);
    }

    public CategoryResponse findById(Long id) {
        Category category = getCategory(id);

        return categoryMapper.toResponse(category);
    }

    public CategoryResponse save(CategoryRequest request) {
        if (categoryRepository.findByNameAndActiveTrue(request.getName()).isPresent()) {
            throw new DuplicateResourceException("La categoría ya existe.");
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category current = getCategory(id);

        if (!current.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.findByNameAndActiveTrue(request.getName()).isPresent()) {
            throw new DuplicateResourceException("La categoría ya existe.");
        }

        categoryMapper.updateEntity(current, request);
        Category updated = categoryRepository.save(current);

        return categoryMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Category category = getCategory(id);

        category.setActive(false);

        categoryRepository.save(category);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    public void test() {
        System.out.println(categoryRepository.findAllByActiveTrue());
    }
}
