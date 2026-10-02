package com.osulaa.possystem.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.sale.SaleItemRequest;
import com.osulaa.possystem.dto.sale.SaleRequest;
import com.osulaa.possystem.dto.sale.SaleResponse;
import com.osulaa.possystem.entity.Client;
import com.osulaa.possystem.entity.Product;
import com.osulaa.possystem.entity.Sale;
import com.osulaa.possystem.entity.SaleDetail;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.exception.BadRequestException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.mapper.SaleDetailMapper;
import com.osulaa.possystem.mapper.SaleMapper;
import com.osulaa.possystem.repository.ClientRepository;
import com.osulaa.possystem.repository.ProductRepository;
import com.osulaa.possystem.repository.SaleDetailRepository;
import com.osulaa.possystem.repository.SaleRepository;
import com.osulaa.possystem.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;
    private final SaleDetailMapper saleDetailMapper;
    private final PageMapper pageMapper;

    public PageResponse<SaleResponse> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Sale> sales;

        if (search == null || search.isBlank()) {
            sales = saleRepository.findAll(pageable);
        } else {
            sales = saleRepository.findByClient_NameContainingIgnoreCase(search, pageable);
        }

        return pageMapper.toPageResponse(sales, saleMapper::toResponse);
    }

    public SaleResponse findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        SaleResponse response = saleMapper.toResponse(sale);

        response.setItems(
                saleDetailMapper.toResponseList(
                        saleDetailRepository.findBySale_Id(id)));

        return response;
    }

    public SaleResponse save(SaleRequest request) {
        Client client = clientRepository.findByIdAndActiveTrue(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        User user = userRepository.findByIdAndActiveTrue(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("La venta debe contener al menos un producto");
        }

        double subtotalSale = 0;
        List<SaleDetail> details = new ArrayList<>();
        Set<Long> products = new HashSet<>();

        for (SaleItemRequest item : request.getItems()) {

            if (item.getQuantity() <= 0) {
                throw new BadRequestException("La cantidad debe ser mayor a cero");
            }

            Product product = productRepository.findByIdAndActiveTrue(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + item.getProductId()));

            if (!products.add(item.getProductId())) {
                throw new BadRequestException("El producto ya se encuentra en la venta");
            }

            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + product.getName());
            }

            double subtotalProduct = product.getSalePrice() * item.getQuantity();
            subtotalSale += subtotalProduct;

            SaleDetail detail = SaleDetail.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(product.getSalePrice())
                    .subtotal(subtotalProduct)
                    .build();

            details.add(detail);

            product.setStock(product.getStock() - item.getQuantity());

            productRepository.save(product);
        }

        double discount = request.getDiscount() == null ? 0 : request.getDiscount();

        if (discount < 0) {
            throw new BadRequestException("El descuento no puede ser negativo");
        }

        if (discount > subtotalSale) {
            throw new BadRequestException("El descuento no puede ser mayor al subtotal");
        }

        double total = subtotalSale - discount;

        Sale sale = Sale.builder()
                .folio(generarFolio())
                .saleDate(LocalDateTime.now())
                .subtotal(subtotalSale)
                .discount(discount)
                .total(total)
                .client(client)
                .user(user)
                .status(SaleStatus.COMPLETED)
                .build();

        sale = saleRepository.save(sale);

        for (SaleDetail detail : details) {
            detail.setSale(sale);

            saleDetailRepository.save(detail);
        }

        SaleResponse response = saleMapper.toResponse(sale);

        response.setItems(saleDetailMapper.toResponseList(details));

        return response;
    }

    public SaleResponse cancel(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        /*
         * if (sale.getStatus() != SaleStatus.COMPLETED) {
         * throw new BadRequestException(
         * "La venta no puede cancelarse porque su estatus actual es: " +
         * sale.getStatus());
         * }
         */

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new BadRequestException("La venta ya fue cancelada");
        }

        List<SaleDetail> details = saleDetailRepository.findBySale_Id(id);

        for (SaleDetail detail : details) {
            Product product = detail.getProduct();

            product.setStock(product.getStock() + detail.getQuantity());

            productRepository.save(product);
        }

        sale.setStatus(SaleStatus.CANCELLED);

        Sale cancelledSale = saleRepository.save(sale);

        SaleResponse response = saleMapper.toResponse(cancelledSale);

        response.setItems(saleDetailMapper.toResponseList(details));

        return response;
    }

    private String generarFolio() {
        return "VTA-" + System.currentTimeMillis();
    }
}
