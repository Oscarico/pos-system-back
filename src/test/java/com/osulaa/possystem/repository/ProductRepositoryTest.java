package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.osulaa.possystem.entity.Category;
import com.osulaa.possystem.entity.Product;

@DataJpaTest
@Transactional // 👈 Revierte todas las inserciones automáticamente tras cada @Test
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Product activeProduct;
    private Product inactiveProduct;
    private Product outOfStockProduct;
    private Product lowStockProduct;

    @BeforeEach
    void setUp() {
        // No usas deleteAll() ni deleteAllInBatch(), usas nombres dinámicos o sufijos
        // únicos para evitar choques
        category = Category.builder()
                .name("Bebidas Test " + System.currentTimeMillis())
                .active(true)
                .build();
        category = categoryRepository.save(category);

        activeProduct = Product.builder()
                .barcode("7501" + System.currentTimeMillis() + "1")
                .name("Coca Cola Test")
                .description("Refresco")
                .purchasePrice(15.0)
                .salePrice(20.0)
                .stock(50)
                .active(true)
                .category(category)
                .build();

        inactiveProduct = Product.builder()
                .barcode("7501" + System.currentTimeMillis() + "2")
                .name("Pepsi Test")
                .description("Refresco")
                .purchasePrice(14.0)
                .salePrice(19.0)
                .stock(30)
                .active(false)
                .category(category)
                .build();

        outOfStockProduct = Product.builder()
                .barcode("7501" + System.currentTimeMillis() + "3")
                .name("Sabritas Test")
                .description("Botana")
                .purchasePrice(10.0)
                .salePrice(15.0)
                .stock(0)
                .active(true)
                .category(category)
                .build();

        lowStockProduct = Product.builder()
                .barcode("7501" + System.currentTimeMillis() + "4")
                .name("Galletas Test")
                .description("Galletas")
                .purchasePrice(8.0)
                .salePrice(12.0)
                .stock(5)
                .active(true)
                .category(category)
                .build();

        List<Product> savedProducts = productRepository.saveAll(List.of(
                activeProduct,
                inactiveProduct,
                outOfStockProduct,
                lowStockProduct));

        activeProduct = savedProducts.get(0);
        inactiveProduct = savedProducts.get(1);
        outOfStockProduct = savedProducts.get(2);
        lowStockProduct = savedProducts.get(3);
    }

    @Test
    void findByIdAndActiveTrue_deberiaEncontrarProductoActivo() {
        Optional<Product> result = productRepository.findByIdAndActiveTrue(activeProduct.getId());

        assertTrue(result.isPresent());
        assertEquals("Coca Cola Test", result.get().getName());
    }

    @Test
    void findByIdAndActiveTrue_noDeberiaEncontrarProductoInactivo() {
        Optional<Product> result = productRepository.findByIdAndActiveTrue(inactiveProduct.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByNameAndActiveTrue_deberiaEncontrarProducto() {
        Optional<Product> result = productRepository.findByNameAndActiveTrue("Coca Cola Test");

        assertTrue(result.isPresent());
    }

    @Test
    void findByBarcodeAndActiveTrue_deberiaEncontrarProducto() {
        Optional<Product> result = productRepository.findByBarcodeAndActiveTrue(activeProduct.getBarcode());

        assertTrue(result.isPresent());
        assertEquals("Coca Cola Test", result.get().getName());
    }

    @Test
    void findByActiveTrue_deberiaRegresarSoloProductosActivos() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Page<Product> result = productRepository.findByActiveTrue(pageable);

        assertTrue(result.getContent().stream().allMatch(Product::getActive));
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrue_deberiaBuscarPorNombre() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> result = productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue("cOcA", pageable);

        assertTrue(result.getTotalElements() >= 1);
    }

    @Test
    void countByActiveTrue_deberiaContarProductosActivos() {
        long result = productRepository.countByActiveTrue();

        assertTrue(result >= 3);
    }

    @Test
    void countActiveLowStock_deberiaContarProductosConStockBajo() {
        long result = productRepository.countActiveLowStock(10);

        assertTrue(result >= 1);
    }

    @Test
    void countActiveOutOfStock_deberiaContarProductosSinStock() {
        long result = productRepository.countActiveOutOfStock();

        assertTrue(result >= 1);
    }
}