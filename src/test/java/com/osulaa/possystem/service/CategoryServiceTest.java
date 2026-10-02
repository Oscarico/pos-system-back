package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.osulaa.possystem.dto.category.CategoryRequest;
import com.osulaa.possystem.dto.category.CategoryResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.entity.Category;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.CategoryMapper;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private Page<Category> categoryPage;

    @Mock
    private PageResponse<CategoryResponse> pageResponse;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {

        category = Category.builder()
                .id(1L)
                .name("Electrónica")
                .description("Productos electrónicos")
                .active(true)
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electrónica")
                .description("Productos electrónicos")
                .active(true)
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electrónica");
        categoryRequest.setDescription("Productos electrónicos");
    }

    @Test
    void findAll_deberiaRegresarCategoriasSinBusqueda() {

        when(categoryRepository.findByActiveTrue(any(Pageable.class)))
                .thenReturn(categoryPage);

        when(pageMapper.<Category, CategoryResponse>toPageResponse(
                eq(categoryPage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<CategoryResponse> result = categoryService.findAll(
                0,
                10,
                "name",
                "asc",
                "");

        assertNotNull(result);

        verify(categoryRepository).findByActiveTrue(any(Pageable.class));

        verify(pageMapper).toPageResponse(
                eq(categoryPage),
                any(Function.class));
    }

    @Test
    void findAll_deberiaBuscarPorNombre() {

        when(categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(
                eq("Elect"),
                any(Pageable.class)))
                .thenReturn(categoryPage);

        when(pageMapper.<Category, CategoryResponse>toPageResponse(
                eq(categoryPage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<CategoryResponse> result = categoryService.findAll(
                0,
                10,
                "name",
                "asc",
                "Elect");

        assertNotNull(result);

        verify(categoryRepository).findByNameContainingIgnoreCaseAndActiveTrue(
                eq("Elect"),
                any(Pageable.class));

        verify(pageMapper).toPageResponse(
                eq(categoryPage),
                any(Function.class));
    }

    @Test
    void findById_deberiaRegresarCategoria() {

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(categoryResponse);

        CategoryResponse result = categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electrónica", result.getName());
        assertEquals("Productos electrónicos", result.getDescription());

        verify(categoryRepository).findByIdAndActiveTrue(1L);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.findById(1L));

        verify(categoryRepository).findByIdAndActiveTrue(1L);
    }

    @Test
    void save_deberiaGuardarCategoria() {

        when(categoryRepository.findByNameAndActiveTrue("Electrónica"))
                .thenReturn(Optional.empty());

        when(categoryMapper.toEntity(categoryRequest))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(categoryResponse);

        CategoryResponse result = categoryService.save(categoryRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electrónica", result.getName());

        verify(categoryRepository)
                .findByNameAndActiveTrue("Electrónica");

        verify(categoryMapper)
                .toEntity(categoryRequest);

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void save_deberiaLanzarExcepcionSiCategoriaYaExiste() {

        when(categoryRepository.findByNameAndActiveTrue("Electrónica"))
                .thenReturn(Optional.of(category));

        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.save(categoryRequest));

        verify(categoryRepository)
                .findByNameAndActiveTrue("Electrónica");
    }

    @Test
    void update_deberiaActualizarCategoria() {

        CategoryRequest request = new CategoryRequest();
        request.setName("Computación");
        request.setDescription("Productos de computación");

        CategoryResponse updatedResponse = CategoryResponse.builder()
                .id(1L)
                .name("Computación")
                .description("Productos de computación")
                .active(true)
                .build();

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(updatedResponse);

        CategoryResponse result = categoryService.update(1L, request);

        assertNotNull(result);
        assertEquals("Computación", result.getName());
        assertEquals(
                "Productos de computación",
                result.getDescription());

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);

        verify(categoryMapper)
                .updateEntity(category, request);

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void update_deberiaLanzarExcepcionSiNuevoNombreYaExiste() {

        CategoryRequest request = new CategoryRequest();
        request.setName("Computación");
        request.setDescription("Productos de computación");

        Category anotherCategory = Category.builder()
                .id(2L)
                .name("Computación")
                .active(true)
                .build();

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.findByNameAndActiveTrue("Computación"))
                .thenReturn(Optional.of(anotherCategory));

        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.update(1L, request));

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);

        verify(categoryRepository)
                .findByNameAndActiveTrue("Computación");
    }

    @Test
    void delete_deberiaDesactivarCategoria() {

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(category));

        categoryService.delete(1L);

        assertEquals(false, category.getActive());

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);

        verify(categoryRepository)
                .save(category);
    }

    @Test
    void delete_deberiaLanzarExcepcionSiNoExiste() {

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.delete(1L));

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);
    }
}