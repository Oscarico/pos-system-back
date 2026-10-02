package com.osulaa.possystem.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.sale.SaleDetailResponse;
import com.osulaa.possystem.dto.sale.SaleItemRequest;
import com.osulaa.possystem.dto.sale.SaleRequest;
import com.osulaa.possystem.dto.sale.SaleResponse;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.exception.BadRequestException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.security.SecurityConfig;
import com.osulaa.possystem.service.SaleService;

@WebMvcTest(SaleController.class)
@AutoConfigureMockMvc // Por defecto addFilters es true
@Import(SecurityConfig.class) // Importa tu SecurityConfig si tus reglas de endpoint están ahí
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SaleService saleService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private SaleResponse sampleSaleResponse;
    private SaleDetailResponse sampleDetailResponse;

    @BeforeEach
    void setUp() {
        sampleDetailResponse = SaleDetailResponse.builder()
                .id(1L)
                .productId(10L)
                .productName("Laptop Gamer")
                .quantity(2)
                .unitPrice(1500.0)
                .subtotal(3000.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleSaleResponse = SaleResponse.builder()
                .id(1L)
                .folio("VTA-123456789")
                .saleDate(LocalDateTime.now())
                .clientName("Javier López")
                .username("admin")
                .subtotal(3000.0)
                .discount(100.0)
                .total(2900.0)
                .status(SaleStatus.COMPLETED)
                .items(List.of(sampleDetailResponse))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Tests para GET /sales")
    class GetAllSalesTests {

        @Test
        @WithMockUser(roles = "CASHIER")
        @DisplayName("Debe listar ventas paginadas con valores por defecto")
        void getAll_conParametrosPorDefecto_deberiaRetornarListaPaginada() throws Exception {
            PageResponse<SaleResponse> pageResponse = PageResponse.<SaleResponse>builder()
                    .items(List.of(sampleSaleResponse))
                    .page(0)
                    .size(10)
                    .totalItems(1L)
                    .totalPages(1)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();

            given(saleService.findAll(0, 10, "saleDate", "desc", null))
                    .willReturn(pageResponse);

            mockMvc.perform(get("/sales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Lista de ventas obtenida correctamente"))
                    .andExpect(jsonPath("$.data.items[0].id").value(1))
                    .andExpect(jsonPath("$.data.items[0].folio").value("VTA-123456789"))
                    .andExpect(jsonPath("$.data.items[0].clientName").value("Javier López"))
                    .andExpect(jsonPath("$.data.totalItems").value(1))
                    .andExpect(jsonPath("$.data.hasNext").value(false));

            verify(saleService).findAll(0, 10, "saleDate", "desc", null);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe listar ventas filtrando por término de búsqueda")
        void getAll_conParametroBusqueda_deberiaFiltrarPorCliente() throws Exception {
            PageResponse<SaleResponse> pageResponse = PageResponse.<SaleResponse>builder()
                    .items(List.of(sampleSaleResponse))
                    .page(0)
                    .size(5)
                    .totalItems(1L)
                    .totalPages(1)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();

            given(saleService.findAll(0, 5, "id", "asc", "Javier"))
                    .willReturn(pageResponse);

            mockMvc.perform(get("/sales")
                    .param("page", "0")
                    .param("size", "5")
                    .param("sortBy", "id")
                    .param("sortDir", "asc")
                    .param("search", "Javier"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.items[0].clientName").value("Javier López"));

            verify(saleService).findAll(0, 5, "id", "asc", "Javier");
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Debe denegar acceso si el usuario no tiene rol ADMIN ni CASHIER")
        void getAll_usuarioSinRolPermitido_deberiaRetornarForbidden() throws Exception {
            mockMvc.perform(get("/sales"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests para GET /sales/{id}")
    class GetSaleByIdTests {

        @Test
        @WithMockUser(roles = "CASHIER")
        @DisplayName("Debe obtener una venta por ID exitosamente")
        void getById_idExistente_deberiaRetornarVenta() throws Exception {
            given(saleService.findById(1L)).willReturn(sampleSaleResponse);

            mockMvc.perform(get("/sales/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Venta encontrada"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.folio").value("VTA-123456789"))
                    .andExpect(jsonPath("$.data.items[0].productName").value("Laptop Gamer"));

            verify(saleService).findById(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 404 cuando la venta no existe")
        void getById_idInexistente_deberiaRetornarNotFound() throws Exception {
            given(saleService.findById(99L))
                    .willThrow(new ResourceNotFoundException("Venta no encontrada"));

            mockMvc.perform(get("/sales/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(saleService).findById(99L);
        }
    }

    @Nested
    @DisplayName("Tests para POST /sales")
    class SaveSaleTests {

        @Test
        @WithMockUser(roles = "CASHIER")
        @DisplayName("Debe registrar una venta exitosamente")
        void save_requestValido_deberiaCrearVenta() throws Exception {
            SaleItemRequest itemRequest = new SaleItemRequest();
            itemRequest.setProductId(10L);
            itemRequest.setQuantity(2);

            SaleRequest request = new SaleRequest();
            request.setClientId(1L);
            request.setUserId(2L);
            request.setDiscount(100.0);
            request.setItems(List.of(itemRequest));

            given(saleService.save(any(SaleRequest.class))).willReturn(sampleSaleResponse);

            mockMvc.perform(post("/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Venta registrada exitosamente"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.total").value(2900.0));

            verify(saleService).save(any(SaleRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe fallar validación cuando la lista de ítems está vacía")
        void save_itemsVacios_deberiaRetornarBadRequest() throws Exception {
            SaleRequest request = new SaleRequest();
            request.setClientId(1L);
            request.setUserId(2L);
            request.setDiscount(0.0);
            request.setItems(List.of());

            mockMvc.perform(post("/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "CASHIER")
        @DisplayName("Debe retornar 400 cuando el stock es insuficiente (excepción de negocio)")
        void save_stockInsuficiente_deberiaRetornarBadRequest() throws Exception {
            SaleItemRequest itemRequest = new SaleItemRequest();
            itemRequest.setProductId(10L);
            itemRequest.setQuantity(50);

            SaleRequest request = new SaleRequest();
            request.setClientId(1L);
            request.setUserId(2L);
            request.setDiscount(0.0);
            request.setItems(List.of(itemRequest));

            given(saleService.save(any(SaleRequest.class)))
                    .willThrow(new BadRequestException("Stock insuficiente para el producto: Laptop Gamer"));

            mockMvc.perform(post("/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(saleService).save(any(SaleRequest.class));
        }
    }

    @Nested
    @DisplayName("Tests para PATCH /sales/{id}/cancel")
    class CancelSaleTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe cancelar una venta exitosamente cuando el usuario es ADMIN")
        void cancel_comoAdmin_deberiaCancelarVenta() throws Exception {
            SaleResponse cancelledResponse = SaleResponse.builder()
                    .id(1L)
                    .folio("VTA-123456789")
                    .status(SaleStatus.CANCELLED)
                    .build();

            given(saleService.cancel(1L)).willReturn(cancelledResponse);

            mockMvc.perform(patch("/sales/{id}/cancel", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Venta cancelada exitosamente"))
                    .andExpect(jsonPath("$.data.status").value("CANCELLED"));

            verify(saleService).cancel(1L);
        }

        @Test
        @WithMockUser(roles = "CASHIER")
        @DisplayName("Debe denegar cancelación si el usuario es CASHIER (solo ADMIN puede cancelar)")
        void cancel_comoCashier_deberiaRetornarForbidden() throws Exception {
            mockMvc.perform(patch("/sales/{id}/cancel", 1L))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 400 cuando la venta ya está cancelada")
        void cancel_ventaYaCancelada_deberiaRetornarBadRequest() throws Exception {
            given(saleService.cancel(1L))
                    .willThrow(new BadRequestException("La venta ya fue cancelada"));

            mockMvc.perform(patch("/sales/{id}/cancel", 1L))
                    .andExpect(status().isBadRequest());

            verify(saleService).cancel(1L);
        }
    }
}