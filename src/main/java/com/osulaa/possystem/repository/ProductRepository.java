package com.osulaa.possystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.osulaa.possystem.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndActiveTrue(Long id);

    Optional<Product> findByNameAndActiveTrue(String name);

    Optional<Product> findByBarcodeAndActiveTrue(String barcode);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable);

    long countByActiveTrue();

    long countByActiveTrueAndStock(Integer stock);

    long countByActiveTrueAndStockLessThan(Integer stock);

    @Query("""
            SELECT COUNT(p)
            FROM Product p
            WHERE p.active = true
            AND p.stock > 0
            AND p.stock < :limit
            """)
    long countActiveLowStock(@Param("limit") Integer limit);

    @Query("""
            SELECT COUNT(p)
            FROM Product p
            WHERE p.active = true
            AND p.stock = 0
            """)
    long countActiveOutOfStock();

}
