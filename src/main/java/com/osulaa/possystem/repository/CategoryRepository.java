package com.osulaa.possystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.osulaa.possystem.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Optional<Category> findByName(String name);

    // boolean existsByName(String name);

    // List<Category> findByActiveTrue();

    List<Category> findAllByActiveTrue();

    Optional<Category> findByNameAndActiveTrue(String name);

    Optional<Category> findByIdAndActiveTrue(Long id);

    Page<Category> findByActiveTrue(Pageable pageable);

    Page<Category> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable);
}
