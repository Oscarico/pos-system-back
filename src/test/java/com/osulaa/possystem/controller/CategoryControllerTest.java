package com.osulaa.possystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osulaa.possystem.dto.category.CategoryRequest;
import com.osulaa.possystem.dto.category.CategoryResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.repository.CategoryRepository;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.CategoryService;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean // 👈 Necesario para resolver la dependencia en JwtAuthenticationFilter
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getAll_deberiaRegresarListaDeCategorias() throws Exception {

        CategoryResponse category = CategoryResponse.builder()
                .id(1L)
                .name("Bebidas")
                .description("Bebidas frías y calientes")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PageResponse<CategoryResponse> pageResponse = PageResponse.<CategoryResponse>builder()
                .items(List.of(category))
                .page(0)
                .size(10)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(categoryService.findAll(0, 10, "name", "asc", null))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].name").value("Bebidas"))
                .andExpect(jsonPath("$.data.items[0].description")
                        .value("Bebidas frías y calientes"));

        verify(categoryService)
                .findAll(0, 10, "name", "asc", null);
    }

    @Test
    void getAll_deberiaAceptarParametrosDeBusqueda() throws Exception {

        CategoryResponse category = CategoryResponse.builder()
                .id(1L)
                .name("Bebidas")
                .description("Bebidas frías")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PageResponse<CategoryResponse> pageResponse = PageResponse.<CategoryResponse>builder()
                .items(List.of(category))
                .page(0)
                .size(10)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(categoryService.findAll(
                1,
                20,
                "description",
                "desc",
                "bebidas"))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/categories")
                .param("page", "1")
                .param("size", "20")
                .param("sortBy", "description")
                .param("sortDir", "desc")
                .param("search", "bebidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].name").value("Bebidas"));

        verify(categoryService)
                .findAll(1, 20, "description", "desc", "bebidas");
    }

    @Test
    void getById_deberiaRegresarCategoria() throws Exception {

        CategoryResponse category = CategoryResponse.builder()
                .id(1L)
                .name("Bebidas")
                .description("Bebidas frías")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(categoryService.findById(1L))
                .thenReturn(category);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Bebidas"))
                .andExpect(jsonPath("$.data.description").value("Bebidas frías"));

        verify(categoryService).findById(1L);
    }

    @Test
    void save_deberiaCrearCategoria() throws Exception {

        CategoryRequest request = new CategoryRequest();
        request.setName("Bebidas");
        request.setDescription("Bebidas frías");

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Bebidas")
                .description("Bebidas frías")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(categoryService.save(any(CategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Bebidas"))
                .andExpect(jsonPath("$.data.description").value("Bebidas frías"));

        verify(categoryService).save(any(CategoryRequest.class));
    }

    @Test
    void save_deberiaRechazarNombreVacio() throws Exception {

        CategoryRequest request = new CategoryRequest();
        request.setName("");
        request.setDescription("Bebidas frías");

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(categoryService, org.mockito.Mockito.never())
                .save(any(CategoryRequest.class));
    }

    @Test
    void update_deberiaActualizarCategoria() throws Exception {

        CategoryRequest request = new CategoryRequest();
        request.setName("Bebidas actualizadas");
        request.setDescription("Nueva descripción");

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Bebidas actualizadas")
                .description("Nueva descripción")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(categoryService.update(eq(1L), any(CategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Bebidas actualizadas"))
                .andExpect(jsonPath("$.data.description")
                        .value("Nueva descripción"));

        verify(categoryService)
                .update(eq(1L), any(CategoryRequest.class));
    }

    @Test
    void delete_deberiaEliminarCategoria() throws Exception {

        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isOk());

        verify(categoryService).delete(1L);
    }
}