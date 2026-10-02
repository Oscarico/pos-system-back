package com.osulaa.possystem.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category activeCategory;
    private Category inactiveCategory;
    private Category anotherActiveCategory;

    @BeforeEach
    void setUp() {

        activeCategory = Category.builder()
                .name("Bebidas Test " + System.currentTimeMillis())
                .active(true)
                .build();

        inactiveCategory = Category.builder()
                .name("Botanas Test " + System.currentTimeMillis())
                .active(false)
                .build();

        anotherActiveCategory = Category.builder()
                .name("Limpieza Test " + System.currentTimeMillis())
                .active(true)
                .build();

        List<Category> savedCategories = categoryRepository.saveAll(List.of(
                activeCategory,
                inactiveCategory,
                anotherActiveCategory));

        activeCategory = savedCategories.get(0);
        inactiveCategory = savedCategories.get(1);
        anotherActiveCategory = savedCategories.get(2);
    }

    @Test
    void findAllByActiveTrue_deberiaRegresarSoloCategoriasActivas() {

        List<Category> result = categoryRepository.findAllByActiveTrue();

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(Category::getActive));
    }

    @Test
    void findByNameAndActiveTrue_deberiaEncontrarCategoriaActiva() {

        Optional<Category> result = categoryRepository
                .findByNameAndActiveTrue(activeCategory.getName());

        assertTrue(result.isPresent());
        assertEquals(activeCategory.getName(), result.get().getName());
    }

    @Test
    void findByNameAndActiveTrue_noDeberiaEncontrarCategoriaInactiva() {

        Optional<Category> result = categoryRepository
                .findByNameAndActiveTrue(inactiveCategory.getName());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndActiveTrue_deberiaEncontrarCategoriaActiva() {

        Optional<Category> result = categoryRepository
                .findByIdAndActiveTrue(activeCategory.getId());

        assertTrue(result.isPresent());
        assertEquals(activeCategory.getName(), result.get().getName());
    }

    @Test
    void findByIdAndActiveTrue_noDeberiaEncontrarCategoriaInactiva() {

        Optional<Category> result = categoryRepository
                .findByIdAndActiveTrue(inactiveCategory.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByActiveTrue_deberiaRegresarSoloCategoriasActivas() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("name").ascending());

        Page<Category> result = categoryRepository.findByActiveTrue(pageable);

        assertTrue(result.getTotalElements() >= 2);
        assertTrue(result.getContent().stream().allMatch(Category::getActive));
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrue_deberiaBuscarPorNombre() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> result = categoryRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        "bebIDas",
                        pageable);

        assertTrue(result.getTotalElements() >= 1);
        assertTrue(result.getContent().stream()
                .anyMatch(category -> category.getName().equals(activeCategory.getName())));
    }
}