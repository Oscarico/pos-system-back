package com.osulaa.possystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.osulaa.possystem.entity.SaleDetail;
import com.osulaa.possystem.repository.projection.ProductSalesProjection;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    List<SaleDetail> findBySale_Id(Long saleId);

    @Query("""
            SELECT
                p.id AS productId,
                p.name AS productName,
                SUM(sd.quantity) AS totalQuantitySold,
                SUM(sd.subtotal) AS totalRevenue
            FROM SaleDetail sd
            JOIN sd.sale s
            JOIN sd.product p
            WHERE s.status = com.osulaa.possystem.enums.SaleStatus.COMPLETED
            AND s.saleDate >= :start
            AND s.saleDate < :end
            GROUP BY p.id, p.name
            ORDER BY SUM(sd.quantity) DESC
            """)
    List<ProductSalesProjection> findProductSalesReport(@Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
