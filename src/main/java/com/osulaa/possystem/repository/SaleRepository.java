package com.osulaa.possystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.osulaa.possystem.entity.Sale;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.projection.SalesChartProjection;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Page<Sale> findAll(Pageable pageable);

    Page<Sale> findByClient_NameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Sale> findByFolio(String folio);

    @Query("""
            SELECT COUNT(s)
            FROM Sale s
            WHERE s.status = 'COMPLETED'
            AND s.saleDate >= :start
            AND s.saleDate < :end
            """)
    Long countCompletedSalesBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT COALESCE(SUM(s.total), 0)
            FROM Sale s
            WHERE s.status = 'COMPLETED'
            AND s.saleDate >= :start
            AND s.saleDate < :end
            """)
    Double sumCompletedSalesBetween(
            LocalDateTime start,
            LocalDateTime end);

    @Query("""
            SELECT COUNT(s)
            FROM Sale s
            WHERE s.status = :status
            AND s.saleDate >= :start
            AND s.saleDate < :end
            """)
    long countByStatusAndSaleDateBetween(
            @Param("status") SaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
            SELECT COALESCE(SUM(s.total), 0)
            FROM Sale s
            WHERE s.status = :status
            AND s.saleDate >= :start
            AND s.saleDate < :end
            """)
    Double sumTotalByStatusAndSaleDateBetween(
            @Param("status") SaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    long countByStatus(SaleStatus status);

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT COALESCE(SUM(s.discount), 0)
            FROM Sale s
            WHERE s.status = :status
            AND s.saleDate >= :start
            AND s.saleDate < :end
            """)
    Double sumDiscountByStatusAndSaleDateBetween(
            @Param("status") SaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
            SELECT
            CAST(s.saleDate AS LocalDate) AS date,
            SUM(s.total) AS total
            FROM Sale s
            WHERE s.status = com.osulaa.possystem.enums.SaleStatus.COMPLETED
            AND s.saleDate >= :start
            AND s.saleDate < :end
            GROUP BY CAST(s.saleDate AS LocalDate)
            ORDER BY CAST(s.saleDate AS LocalDate)
            """)
    List<SalesChartProjection> findSalesChart(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
