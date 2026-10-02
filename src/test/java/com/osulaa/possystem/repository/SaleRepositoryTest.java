package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.osulaa.possystem.entity.Client;
import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.Sale;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.projection.SalesChartProjection;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Client client;

    private Sale completedSale;
    private Sale cancelledSale;
    private Sale anotherCompletedSale;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {

        // =========================
        // ROLE
        // =========================

        Role role = Role.builder()
                .name("ADMIN_TEST_" + System.currentTimeMillis())
                .build();

        role = roleRepository.save(role);

        // =========================
        // USER
        // =========================

        user = User.builder()
                .username("user_test_" + System.currentTimeMillis())
                .password("password")
                .name("Usuario")
                .lastName("Test")
                .active(true)
                .role(role)
                .build();

        user = userRepository.save(user);

        // =========================
        // CLIENT
        // =========================

        client = Client.builder()
                .name("Cliente Test")
                .email("cliente_" + System.currentTimeMillis() + "@test.com")
                .active(true)
                .build();

        client = clientRepository.save(client);

        // =========================
        // FECHAS
        // =========================

        start = LocalDateTime.of(2099, 8, 10, 0, 0);
        end = LocalDateTime.of(2099, 8, 13, 0, 0);

        // =========================
        // SALE COMPLETED
        // =========================

        completedSale = Sale.builder()
                .folio("FOLIO-TEST-" + System.currentTimeMillis() + "-1")
                .saleDate(LocalDateTime.of(2099, 8, 10, 10, 30))
                .subtotal(100.0)
                .discount(10.0)
                .total(90.0)
                .status(SaleStatus.COMPLETED)
                .user(user)
                .client(client)
                .build();

        // =========================
        // SALE CANCELLED
        // =========================

        cancelledSale = Sale.builder()
                .folio("FOLIO-TEST-" + System.currentTimeMillis() + "-2")
                .saleDate(LocalDateTime.of(2099, 8, 11, 12, 0))
                .subtotal(200.0)
                .discount(20.0)
                .total(180.0)
                .status(SaleStatus.CANCELLED)
                .user(user)
                .client(client)
                .build();

        // =========================
        // ANOTHER COMPLETED SALE
        // =========================

        anotherCompletedSale = Sale.builder()
                .folio("FOLIO-TEST-" + System.currentTimeMillis() + "-3")
                .saleDate(LocalDateTime.of(2099, 8, 12, 15, 0))
                .subtotal(300.0)
                .discount(30.0)
                .total(270.0)
                .status(SaleStatus.COMPLETED)
                .user(user)
                .client(client)
                .build();

        List<Sale> savedSales = saleRepository.saveAll(List.of(
                completedSale,
                cancelledSale,
                anotherCompletedSale));

        completedSale = savedSales.get(0);
        cancelledSale = savedSales.get(1);
        anotherCompletedSale = savedSales.get(2);
    }

    @Test
    void findAll_deberiaRegresarTodasLasVentas() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Sale> result = saleRepository.findAll(pageable);

        assertTrue(result.getContent().stream()
                .anyMatch(s -> s.getId().equals(completedSale.getId())));

        assertTrue(result.getContent().stream()
                .anyMatch(s -> s.getId().equals(cancelledSale.getId())));

        assertTrue(result.getContent().stream()
                .anyMatch(s -> s.getId().equals(anotherCompletedSale.getId())));
    }

    @Test
    void findByClientNameContainingIgnoreCase_deberiaEncontrarVentas() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Sale> result = saleRepository
                .findByClient_NameContainingIgnoreCase("cliente", pageable);

        assertFalse(result.isEmpty());
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void findByFolio_deberiaEncontrarVenta() {

        Optional<Sale> result = saleRepository.findByFolio(
                completedSale.getFolio());

        assertTrue(result.isPresent());
        assertEquals(
                completedSale.getFolio(),
                result.get().getFolio());
    }

    @Test
    void findByFolio_noDeberiaEncontrarVentaInexistente() {

        Optional<Sale> result = saleRepository.findByFolio(
                "FOLIO-INEXISTENTE");

        assertTrue(result.isEmpty());
    }

    @Test
    void countCompletedSalesBetween_deberiaContarVentasCompletadas() {

        Long result = saleRepository.countCompletedSalesBetween(
                start,
                end);

        assertTrue(result >= 2);
    }

    @Test
    void sumCompletedSalesBetween_deberiaSumarVentasCompletadas() {

        Double result = saleRepository.sumCompletedSalesBetween(
                start,
                end);

        assertEquals(360.0, result);
    }

    @Test
    void countByStatusAndSaleDateBetween_deberiaContarPorEstado() {

        long result = saleRepository.countByStatusAndSaleDateBetween(
                SaleStatus.CANCELLED,
                start,
                end);

        assertTrue(result >= 1);
    }

    @Test
    void sumTotalByStatusAndSaleDateBetween_deberiaSumarPorEstado() {

        Double result = saleRepository.sumTotalByStatusAndSaleDateBetween(
                SaleStatus.CANCELLED,
                start,
                end);

        assertEquals(180.0, result);
    }

    @Test
    void countByStatus_deberiaContarVentasPorEstado() {

        long result = saleRepository.countByStatus(
                SaleStatus.COMPLETED);

        assertTrue(result >= 2);
    }

    @Test
    void findBySaleDateBetween_deberiaEncontrarVentasEnRango() {

        List<Sale> result = saleRepository.findBySaleDateBetween(
                start,
                end);

        assertEquals(3, result.size());
    }

    @Test
    void sumDiscountByStatusAndSaleDateBetween_deberiaSumarDescuentos() {

        Double result = saleRepository.sumDiscountByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end);

        assertEquals(40.0, result);
    }

    @Test
    void findSalesChart_deberiaRegresarDatosAgrupadosPorFecha() {

        List<SalesChartProjection> result = saleRepository.findSalesChart(
                start,
                end);

        assertFalse(result.isEmpty());

        assertEquals(2, result.size());

        assertNotNull(result.get(0).getDate());
        assertNotNull(result.get(0).getTotal());
    }
}