package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.product.ProductRequest;
import com.osulaa.possystem.dto.product.ProductResponse;
import com.osulaa.possystem.entity.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .barcode(request.getBarcode())
                .name(request.getName())
                .description(request.getDescription())
                .purchasePrice(request.getPurchasePrice())
                .salePrice(request.getSellPrice())
                .stock(request.getStock())
                .active(true)
                .build();
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .barcode(product.getBarcode())
                .name(product.getName())
                .description(product.getDescription())
                .purchasePrice(product.getPurchasePrice())
                .sellPrice(product.getSalePrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntity(Product product, ProductRequest request) {

        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSellPrice());
        product.setStock(request.getStock());
    }
}
