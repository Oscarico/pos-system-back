package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.osulaa.possystem.entity.Category;
import com.osulaa.possystem.entity.Client;
import com.osulaa.possystem.entity.Product;
import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.Sale;
import com.osulaa.possystem.entity.SaleDetail;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.projection.ProductSalesProjection;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SaleDetailRepositoryTest {

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Sale sale;

    private Sale cancelledSale;

    private Product product1;
    private Product product2;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {

        // =========================
        // ROLE
        // =========================

        Role role = Role.builder()
                .name("ADMIN_DETAIL_TEST_" + System.currentTimeMillis())
                .build();

        role = roleRepository.save(role);

        // =========================
        // USER
        // =========================

        User user = User.builder()
                .username("user_detail_test_" + System.currentTimeMillis())
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

        Client client = Client.builder()
                .name("Cliente Detail Test")
                .email("detail_" + System.currentTimeMillis() + "@test.com")
                .active(true)
                .build();

        client = clientRepository.save(client);

        // =========================
        // CATEGORY
        // =========================

        Category category = Category.builder()
                .name("Categoria Detail Test " + System.currentTimeMillis())
                .active(true)
                .build();

        category = categoryRepository.save(category);

        // =========================
        // PRODUCTS
        // =========================

        product1 = Product.builder()
                .barcode("DETAIL-" + System.currentTimeMillis() + "-1")
                .name("Producto Detail 1")
                .description("Producto de prueba")
                .purchasePrice(50.0)
                .salePrice(100.0)
                .stock(50)
                .active(true)
                .category(category)
                .build();

        product2 = Product.builder()
                .barcode("DETAIL-" + System.currentTimeMillis() + "-2")
                .name("Producto Detail 2")
                .description("Producto de prueba")
                .purchasePrice(25.0)
                .salePrice(50.0)
                .stock(50)
                .active(true)
                .category(category)
                .build();

        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);

        // =========================
        // FECHAS
        // =========================

        start = LocalDateTime.of(2099, 9, 10, 0, 0);
        end = LocalDateTime.of(2099, 9, 13, 0, 0);

        // =========================
        // SALE COMPLETED
        // =========================

        sale = Sale.builder()
                .folio("DETAIL-FOLIO-" + System.currentTimeMillis() + "-1")
                .saleDate(LocalDateTime.of(2099, 9, 10, 10, 0))
                .subtotal(250.0)
                .discount(0.0)
                .total(250.0)
                .status(SaleStatus.COMPLETED)
                .user(user)
                .client(client)
                .build();

        sale = saleRepository.save(sale);

        // =========================
        // SALE CANCELLED
        // =========================

        cancelledSale = Sale.builder()
                .folio("DETAIL-FOLIO-" + System.currentTimeMillis() + "-2")
                .saleDate(LocalDateTime.of(2099, 9, 11, 10, 0))
                .subtotal(100.0)
                .discount(0.0)
                .total(100.0)
                .status(SaleStatus.CANCELLED)
                .user(user)
                .client(client)
                .build();

        cancelledSale = saleRepository.save(cancelledSale);

        // =========================
        // SALE DETAILS
        // =========================

        SaleDetail detail1 = SaleDetail.builder()
                .sale(sale)
                .product(product1)
                .quantity(2)
                .unitPrice(100.0)
                .subtotal(200.0)
                .build();

        SaleDetail detail2 = SaleDetail.builder()
                .sale(sale)
                .product(product2)
                .quantity(1)
                .unitPrice(50.0)
                .subtotal(50.0)
                .build();

        // Detail de venta cancelada
        SaleDetail cancelledDetail = SaleDetail.builder()
                .sale(cancelledSale)
                .product(product1)
                .quantity(5)
                .unitPrice(100.0)
                .subtotal(500.0)
                .build();

        saleDetailRepository.saveAll(List.of(
                detail1,
                detail2,
                cancelledDetail));
    }

    @Test
    void findBySaleId_deberiaEncontrarDetallesDeLaVenta() {

        List<SaleDetail> result = saleDetailRepository
                .findBySale_Id(sale.getId());

        assertEquals(2, result.size());

        assertTrue(result.stream()
                .allMatch(detail -> detail.getSale().getId().equals(sale.getId())));
    }

    @Test
    void findBySaleId_noDeberiaEncontrarDetallesDeOtraVenta() {

        List<SaleDetail> result = saleDetailRepository
                .findBySale_Id(cancelledSale.getId());

        assertEquals(1, result.size());

        assertEquals(
                cancelledSale.getId(),
                result.get(0).getSale().getId());
    }

    @Test
    void findBySaleId_deberiaRegresarListaVaciaSiNoExisteVenta() {

        List<SaleDetail> result = saleDetailRepository
                .findBySale_Id(999999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findProductSalesReport_deberiaGenerarReporteDeProductos() {

        List<ProductSalesProjection> result = saleDetailRepository
                .findProductSalesReport(start, end);

        assertFalse(result.isEmpty());

        assertEquals(2, result.size());

        ProductSalesProjection first = result.get(0);

        assertTrue(first.getProductId() != null);
        assertTrue(first.getProductName() != null);
        assertTrue(first.getTotalQuantitySold() != null);
        assertTrue(first.getTotalRevenue() != null);
    }

    @Test
    void findProductSalesReport_deberiaExcluirVentasCanceladas() {

        List<ProductSalesProjection> result = saleDetailRepository
                .findProductSalesReport(start, end);

        ProductSalesProjection product1Result = result.stream()
                .filter(p -> p.getProductId().equals(product1.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(2, product1Result.getTotalQuantitySold());
        assertEquals(200.0, product1Result.getTotalRevenue());
    }

    @Test
    void findProductSalesReport_deberiaOrdenarPorCantidadVendidaDescendente() {

        List<ProductSalesProjection> result = saleDetailRepository
                .findProductSalesReport(start, end);

        assertEquals(2, result.size());

        assertTrue(
                result.get(0).getTotalQuantitySold() >= result.get(1).getTotalQuantitySold());
    }
}