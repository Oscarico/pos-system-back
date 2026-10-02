package com.osulaa.possystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.product.ProductRequest;
import com.osulaa.possystem.dto.product.ProductResponse;
import com.osulaa.possystem.entity.Category;
import com.osulaa.possystem.entity.Product;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.mapper.ProductMapper;
import com.osulaa.possystem.repository.CategoryRepository;
import com.osulaa.possystem.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final PageMapper pageMapper;

    public PageResponse<ProductResponse> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products;

        if (search == null || search.isBlank()) {
            products = productRepository.findByActiveTrue(pageable);
        } else {
            products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(search, pageable);
        }

        return pageMapper.toPageResponse(products, productMapper::toResponse);
    }

    public ProductResponse findById(Long id) {
        Product product = getProduct(id);

        return productMapper.toResponse(product);
    }

    public ProductResponse save(ProductRequest request) {
        if (productRepository.findByNameAndActiveTrue(request.getName()).isPresent()) {
            throw new DuplicateResourceException("El producto ya existe.");
        }

        if (productRepository.findByBarcodeAndActiveTrue(request.getBarcode()).isPresent()) {
            throw new DuplicateResourceException("El código de barras ya existe.");
        }

        Product product = productMapper.toEntity(request);

        Category category = categoryRepository.findByIdAndActiveTrue(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada."));

        product.setCategory(category);

        Product saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product current = getProduct(id);

        if (!current.getName().equalsIgnoreCase(request.getName())
                && productRepository.findByNameAndActiveTrue(request.getName()).isPresent()) {
            throw new DuplicateResourceException("El producto ya existe.");
        }

        if (!current.getBarcode().equalsIgnoreCase(request.getBarcode())
                && productRepository.findByBarcodeAndActiveTrue(request.getBarcode()).isPresent()) {
            throw new DuplicateResourceException("El código de barras ya existe.");
        }

        Category category = categoryRepository.findByIdAndActiveTrue(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada."));
        current.setCategory(category);

        productMapper.updateEntity(current, request);
        Product updated = productRepository.save(current);

        return productMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Product product = getProduct(id);

        product.setActive(false);

        productRepository.save(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));
    }
}
