package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.sale.SaleDetailResponse;
import com.osulaa.possystem.dto.sale.SaleResponse;
import com.osulaa.possystem.entity.Client;
import com.osulaa.possystem.entity.Sale;
import com.osulaa.possystem.entity.SaleDetail;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.mapper.SaleDetailMapper;
import com.osulaa.possystem.mapper.SaleMapper;
import com.osulaa.possystem.repository.ClientRepository;
import com.osulaa.possystem.repository.ProductRepository;
import com.osulaa.possystem.repository.SaleDetailRepository;
import com.osulaa.possystem.repository.SaleRepository;
import com.osulaa.possystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleDetailRepository saleDetailRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private SaleDetailMapper saleDetailMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private Page<Sale> salePage;

    @Mock
    private PageResponse<SaleResponse> pageResponse;

    @InjectMocks
    private SaleService saleService;

    private Sale sale;
    private SaleResponse saleResponse;
    private Client client;
    private User user;

    @BeforeEach
    void setUp() {

        client = Client.builder()
                .id(1L)
                .name("Juan Pérez")
                .active(true)
                .build();

        user = User.builder()
                .id(1L)
                .username("admin")
                .name("Administrador")
                .lastName("Sistema")
                .active(true)
                .build();

        sale = Sale.builder()
                .id(1L)
                .folio("VTA-123456")
                .subtotal(500.0)
                .discount(50.0)
                .total(450.0)
                .status(SaleStatus.COMPLETED)
                .client(client)
                .user(user)
                .build();

        saleResponse = SaleResponse.builder()
                .id(1L)
                .folio("VTA-123456")
                .clientName("Juan Pérez")
                .username("admin")
                .subtotal(500.0)
                .discount(50.0)
                .total(450.0)
                .status(SaleStatus.COMPLETED)
                .build();
    }

    @Test
    void findAll_deberiaRegresarVentasSinBusqueda() {

        when(saleRepository.findAll(any(Pageable.class)))
                .thenReturn(salePage);

        when(pageMapper.toPageResponse(
                eq(salePage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<SaleResponse> result = saleService.findAll(
                0,
                10,
                "saleDate",
                "desc",
                "");

        assertNotNull(result);

        verify(saleRepository).findAll(any(Pageable.class));

        verify(pageMapper).toPageResponse(
                eq(salePage),
                any(Function.class));
    }

    @Test
    void findAll_deberiaBuscarPorCliente() {

        when(saleRepository.findByClient_NameContainingIgnoreCase(
                eq("Juan"),
                any(Pageable.class)))
                .thenReturn(salePage);

        when(pageMapper.toPageResponse(
                eq(salePage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<SaleResponse> result = saleService.findAll(
                0,
                10,
                "saleDate",
                "desc",
                "Juan");

        assertNotNull(result);

        verify(saleRepository).findByClient_NameContainingIgnoreCase(
                eq("Juan"),
                any(Pageable.class));

        verify(pageMapper).toPageResponse(
                eq(salePage),
                any(Function.class));
    }

    @Test
    void findById_deberiaRegresarVentaConDetalles() {

        SaleDetail detail = SaleDetail.builder()
                .id(1L)
                .quantity(2)
                .unitPrice(250.0)
                .subtotal(500.0)
                .build();

        SaleDetailResponse detailResponse = SaleDetailResponse.builder()
                .id(1L)
                .quantity(2)
                .unitPrice(250.0)
                .subtotal(500.0)
                .build();

        when(saleRepository.findById(1L))
                .thenReturn(Optional.of(sale));

        when(saleMapper.toResponse(sale))
                .thenReturn(saleResponse);

        when(saleDetailRepository.findBySale_Id(1L))
                .thenReturn(List.of(detail));

        when(saleDetailMapper.toResponseList(List.of(detail)))
                .thenReturn(List.of(detailResponse));

        SaleResponse result = saleService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("VTA-123456", result.getFolio());
        assertEquals("Juan Pérez", result.getClientName());
        assertEquals("admin", result.getUsername());
        assertEquals(1, result.getItems().size());

        verify(saleRepository).findById(1L);
        verify(saleMapper).toResponse(sale);
        verify(saleDetailRepository).findBySale_Id(1L);
        verify(saleDetailMapper).toResponseList(List.of(detail));
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {

        when(saleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> saleService.findById(1L));

        verify(saleRepository).findById(1L);
    }
}