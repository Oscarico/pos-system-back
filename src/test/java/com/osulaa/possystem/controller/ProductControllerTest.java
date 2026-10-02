package com.osulaa.possystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.product.ProductRequest;
import com.osulaa.possystem.dto.product.ProductResponse;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Helper para construir DTOs de respuesta
    private ProductResponse createProductResponse(Long id, String name, String barcode, Double sellPrice,
            Integer stock) {
        return ProductResponse.builder()
                .id(id)
                .name(name)
                .barcode(barcode)
                .description("Descripción de prueba")
                .purchasePrice(12.50)
                .sellPrice(sellPrice)
                .stock(stock)
                .active(true)
                .categoryId(1L)
                .categoryName("Bebidas")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Helper para construir DTOs de petición con campos válidos
    private ProductRequest createProductRequest(String name, String barcode, Double purchasePrice, Double sellPrice,
            Integer stock) {
        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setBarcode(barcode);
        request.setDescription("Descripción de prueba");
        request.setPurchasePrice(purchasePrice);
        request.setSellPrice(sellPrice);
        request.setStock(stock);
        request.setCategoryId(1L);
        return request;
    }

    @Test
    void getAll_deberiaRegresarPaginaDeProductosConValoresPorDefecto() throws Exception {
        ProductResponse product = createProductResponse(1L, "Coca Cola 600ml", "7501055300077", 18.00, 50);

        PageResponse<ProductResponse> pageResponse = PageResponse.<ProductResponse>builder()
                .items(List.of(product))
                .page(0)
                .size(10)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(productService.findAll(0, 10, "name", "asc", null))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lista de productos obtenida exitosamente."))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].name").value("Coca Cola 600ml"))
                .andExpect(jsonPath("$.data.items[0].barcode").value("7501055300077"))
                .andExpect(jsonPath("$.data.items[0].purchasePrice").value(12.50))
                .andExpect(jsonPath("$.data.items[0].sellPrice").value(18.00))
                .andExpect(jsonPath("$.data.items[0].stock").value(50));

        verify(productService).findAll(0, 10, "name", "asc", null);
    }

    @Test
    void getAll_deberiaAceptarParametrosDeBusquedaYPaginacion() throws Exception {
        ProductResponse product = createProductResponse(1L, "Coca Cola 600ml", "7501055300077", 18.00, 50);

        PageResponse<ProductResponse> pageResponse = PageResponse.<ProductResponse>builder()
                .items(List.of(product))
                .page(0)
                .size(20)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(productService.findAll(0, 20, "sellPrice", "desc", "coca"))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/products")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "sellPrice")
                .param("sortDir", "desc")
                .param("search", "coca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].name").value("Coca Cola 600ml"));

        verify(productService).findAll(0, 20, "sellPrice", "desc", "coca");
    }

    @Test
    void getById_deberiaRegresarProductoPorId() throws Exception {
        ProductResponse product = createProductResponse(1L, "Coca Cola 600ml", "7501055300077", 18.00, 50);

        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto encontrado."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Coca Cola 600ml"))
                .andExpect(jsonPath("$.data.categoryName").value("Bebidas"));

        verify(productService).findById(1L);
    }

    @Test
    void save_deberiaCrearProductoExitosamente() throws Exception {
        ProductRequest request = createProductRequest("Coca Cola 600ml", "7501055300077", 12.50, 18.00, 50);
        ProductResponse response = createProductResponse(1L, "Coca Cola 600ml", "7501055300077", 18.00, 50);

        when(productService.save(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Producto creado correctamente."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Coca Cola 600ml"))
                .andExpect(jsonPath("$.data.sellPrice").value(18.00));

        verify(productService).save(any(ProductRequest.class));
    }

    @Test
    void save_deberiaRechazarRequestInvalido() throws Exception {
        // Enviar DTO vacío para activar validaciones (@NotBlank, @NotNull)
        ProductRequest invalidRequest = new ProductRequest();

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).save(any(ProductRequest.class));
    }

    @Test
    void update_deberiaActualizarProductoExitosamente() throws Exception {
        ProductRequest request = createProductRequest("Coca Cola Zero 600ml", "7501055300077", 13.00, 19.00, 40);
        ProductResponse response = createProductResponse(1L, "Coca Cola Zero 600ml", "7501055300077", 19.00, 40);

        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto actualizado correctamente."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Coca Cola Zero 600ml"))
                .andExpect(jsonPath("$.data.sellPrice").value(19.00));

        verify(productService).update(eq(1L), any(ProductRequest.class));
    }

    @Test
    void delete_deberiaEliminarProductoExitosamente() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto eliminado correctamente."));

        verify(productService).delete(1L);
    }
}