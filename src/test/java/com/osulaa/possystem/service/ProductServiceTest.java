package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private Page<Product> productPage;

    @Mock
    private PageResponse<ProductResponse> pageResponse;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {

        category = Category.builder()
                .id(1L)
                .name("Electrónica")
                .description("Productos electrónicos")
                .active(true)
                .build();

        product = Product.builder()
                .id(1L)
                .barcode("7501234567890")
                .name("Mouse Logitech")
                .description("Mouse inalámbrico")
                .purchasePrice(200.0)
                .salePrice(350.0)
                .stock(10)
                .category(category)
                .active(true)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .barcode("7501234567890")
                .name("Mouse Logitech")
                .description("Mouse inalámbrico")
                .purchasePrice(200.0)
                .sellPrice(350.0)
                .stock(10)
                .categoryId(1L)
                .categoryName("Electrónica")
                .active(true)
                .build();

        productRequest = new ProductRequest();
        productRequest.setBarcode("7501234567890");
        productRequest.setName("Mouse Logitech");
        productRequest.setDescription("Mouse inalámbrico");
        productRequest.setPurchasePrice(200.0);
        productRequest.setSellPrice(350.0);
        productRequest.setStock(10);
        productRequest.setCategoryId(1L);
    }

    @Test
    void findAll_deberiaRegresarProductosSinBusqueda() {

        when(productRepository.findByActiveTrue(any(Pageable.class)))
                .thenReturn(productPage);

        when(pageMapper.toPageResponse(
                eq(productPage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<ProductResponse> result = productService.findAll(
                0,
                10,
                "name",
                "asc",
                "");

        assertNotNull(result);

        verify(productRepository)
                .findByActiveTrue(any(Pageable.class));

        verify(pageMapper)
                .toPageResponse(
                        eq(productPage),
                        any(Function.class));
    }

    @Test
    void findAll_deberiaBuscarPorNombre() {

        when(productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        eq("Mouse"),
                        any(Pageable.class)))
                .thenReturn(productPage);

        when(pageMapper.toPageResponse(
                eq(productPage),
                any(Function.class)))
                .thenReturn(pageResponse);

        PageResponse<ProductResponse> result = productService.findAll(
                0,
                10,
                "name",
                "asc",
                "Mouse");

        assertNotNull(result);

        verify(productRepository)
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        eq("Mouse"),
                        any(Pageable.class));

        verify(pageMapper)
                .toPageResponse(
                        eq(productPage),
                        any(Function.class));
    }

    @Test
    void findById_deberiaRegresarProducto() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mouse Logitech", result.getName());

        verify(productRepository)
                .findByIdAndActiveTrue(1L);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(1L));

        verify(productRepository)
                .findByIdAndActiveTrue(1L);
    }

    @Test
    void save_deberiaGuardarProducto() {

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech"))
                .thenReturn(Optional.empty());

        when(productRepository.findByBarcodeAndActiveTrue(
                "7501234567890"))
                .thenReturn(Optional.empty());

        when(productMapper.toEntity(productRequest))
                .thenReturn(product);

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse result = productService.save(productRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mouse Logitech", result.getName());

        verify(productRepository)
                .findByNameAndActiveTrue("Mouse Logitech");

        verify(productRepository)
                .findByBarcodeAndActiveTrue("7501234567890");

        verify(productMapper)
                .toEntity(productRequest);

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void save_deberiaLanzarExcepcionSiProductoYaExiste() {

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech"))
                .thenReturn(Optional.of(product));

        assertThrows(
                DuplicateResourceException.class,
                () -> productService.save(productRequest));

        verify(productRepository)
                .findByNameAndActiveTrue("Mouse Logitech");
    }

    @Test
    void save_deberiaLanzarExcepcionSiCodigoBarrasYaExiste() {

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech"))
                .thenReturn(Optional.empty());

        when(productRepository.findByBarcodeAndActiveTrue(
                "7501234567890"))
                .thenReturn(Optional.of(product));

        assertThrows(
                DuplicateResourceException.class,
                () -> productService.save(productRequest));

        verify(productRepository)
                .findByNameAndActiveTrue("Mouse Logitech");

        verify(productRepository)
                .findByBarcodeAndActiveTrue("7501234567890");
    }

    @Test
    void save_deberiaLanzarExcepcionSiCategoriaNoExiste() {

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech"))
                .thenReturn(Optional.empty());

        when(productRepository.findByBarcodeAndActiveTrue(
                "7501234567890"))
                .thenReturn(Optional.empty());

        when(productMapper.toEntity(productRequest))
                .thenReturn(product);

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.save(productRequest));

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);
    }

    @Test
    void update_deberiaActualizarProducto() {

        ProductRequest request = new ProductRequest();
        request.setBarcode("7509999999999");
        request.setName("Mouse Logitech MX");
        request.setDescription("Mouse inalámbrico nuevo");
        request.setPurchasePrice(300.0);
        request.setSellPrice(500.0);
        request.setStock(20);
        request.setCategoryId(1L);

        ProductResponse updatedResponse = ProductResponse.builder()
                .id(1L)
                .barcode("7509999999999")
                .name("Mouse Logitech MX")
                .description("Mouse inalámbrico nuevo")
                .purchasePrice(300.0)
                .sellPrice(500.0)
                .stock(20)
                .categoryId(1L)
                .categoryName("Electrónica")
                .active(true)
                .build();

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech MX"))
                .thenReturn(Optional.empty());

        when(productRepository.findByBarcodeAndActiveTrue(
                "7509999999999"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(updatedResponse);

        ProductResponse result = productService.update(1L, request);

        assertNotNull(result);
        assertEquals("Mouse Logitech MX", result.getName());
        assertEquals("7509999999999", result.getBarcode());

        verify(productRepository)
                .findByIdAndActiveTrue(1L);

        verify(productRepository)
                .findByNameAndActiveTrue("Mouse Logitech MX");

        verify(productRepository)
                .findByBarcodeAndActiveTrue("7509999999999");

        verify(categoryRepository)
                .findByIdAndActiveTrue(1L);

        verify(productMapper)
                .updateEntity(product, request);

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void update_deberiaLanzarExcepcionSiNuevoNombreYaExiste() {

        ProductRequest request = new ProductRequest();
        request.setBarcode("7509999999999");
        request.setName("Otro Mouse");
        request.setDescription("Descripción");
        request.setPurchasePrice(300.0);
        request.setSellPrice(500.0);
        request.setStock(20);
        request.setCategoryId(1L);

        Product anotherProduct = Product.builder()
                .id(2L)
                .name("Otro Mouse")
                .barcode("1111111111111")
                .active(true)
                .build();

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameAndActiveTrue(
                "Otro Mouse"))
                .thenReturn(Optional.of(anotherProduct));

        assertThrows(
                DuplicateResourceException.class,
                () -> productService.update(1L, request));

        verify(productRepository)
                .findByIdAndActiveTrue(1L);

        verify(productRepository)
                .findByNameAndActiveTrue("Otro Mouse");
    }

    @Test
    void update_deberiaLanzarExcepcionSiNuevoBarcodeYaExiste() {

        ProductRequest request = new ProductRequest();
        request.setBarcode("1111111111111");
        request.setName("Mouse Logitech MX");
        request.setDescription("Descripción");
        request.setPurchasePrice(300.0);
        request.setSellPrice(500.0);
        request.setStock(20);
        request.setCategoryId(1L);

        Product anotherProduct = Product.builder()
                .id(2L)
                .name("Otro Producto")
                .barcode("1111111111111")
                .active(true)
                .build();

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech MX"))
                .thenReturn(Optional.empty());

        when(productRepository.findByBarcodeAndActiveTrue(
                "1111111111111"))
                .thenReturn(Optional.of(anotherProduct));

        assertThrows(
                DuplicateResourceException.class,
                () -> productService.update(1L, request));

        verify(productRepository)
                .findByIdAndActiveTrue(1L);

        verify(productRepository)
                .findByNameAndActiveTrue("Mouse Logitech MX");

        verify(productRepository)
                .findByBarcodeAndActiveTrue("1111111111111");
    }

    @Test
    void update_deberiaLanzarExcepcionSiCategoriaNoExiste() {

        ProductRequest request = new ProductRequest();
        request.setBarcode("7509999999999");
        request.setName("Mouse Logitech MX");
        request.setDescription("Descripción");
        request.setPurchasePrice(300.0);
        request.setSellPrice(500.0);
        request.setStock(20);
        request.setCategoryId(99L);

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameAndActiveTrue(
                "Mouse Logitech MX"))
                .thenReturn(Optional.empty());

        when(productRepository.findByBarcodeAndActiveTrue(
                "7509999999999"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndActiveTrue(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(1L, request));

        verify(categoryRepository)
                .findByIdAndActiveTrue(99L);
    }

    @Test
    void delete_deberiaDesactivarProducto() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        productService.delete(1L);

        assertEquals(false, product.getActive());

        verify(productRepository)
                .findByIdAndActiveTrue(1L);

        verify(productRepository)
                .save(product);
    }

    @Test
    void delete_deberiaLanzarExcepcionSiNoExiste() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(1L));

        verify(productRepository)
                .findByIdAndActiveTrue(1L);
    }
}