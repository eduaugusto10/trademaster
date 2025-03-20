package com.trademaster.transactions.service;

import com.trademaster.transactions.domain.Product;
import com.trademaster.transactions.domain.mapper.ProductMapper;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.factory.ProductFactory;
import com.trademaster.transactions.model.ProductCreateDTO;
import com.trademaster.transactions.model.ProductResponseDTO;
import com.trademaster.transactions.repository.ProductRepository;
import com.trademaster.transactions.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

        @InjectMocks
        private ProductServiceImpl productService;

        @Mock
        private ProductRepository productRepository;

        @Mock
        private ProductMapper productMapper;

        @Test
        void testSaveProduct_Success() {
            ProductCreateDTO dto = ProductFactory.productCreateDTO();
            Product product = ProductFactory.product();
            Product saved = ProductFactory.product();
            ProductResponseDTO responseDTO = ProductFactory.productResponseDTO();

            when(productMapper.toEntity(dto)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(saved);
            when(productMapper.toResponseDTO(saved)).thenReturn(responseDTO);

            ProductResponseDTO result = productService.saveProduct(dto);

            assertNotNull(result);
            assertEquals(responseDTO, result);
            verify(productRepository).save(product);
        }

        @Test
        void testFindProductById_Success() {
            Product product = ProductFactory.product();
            ProductResponseDTO responseDTO = ProductFactory.productResponseDTO();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

            Optional<ProductResponseDTO> result = productService.findProductById(1L);

            assertTrue(result.isPresent());
            assertEquals(responseDTO, result.get());
        }

        @Test
        void testFindProductById_NotFound() {
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.findProductById(1L));
        }

        @Test
        void testFindAllProducts_Success() {
            Product product1 = ProductFactory.product();
            Product product2 = ProductFactory.product();
            ProductResponseDTO dto1 = ProductFactory.productResponseDTO();
            ProductResponseDTO dto2 = ProductFactory.productResponseDTO();

            when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
            when(productMapper.toResponseDTO(product1)).thenReturn(dto1);
            when(productMapper.toResponseDTO(product2)).thenReturn(dto2);

            List<ProductResponseDTO> result = productService.findAllProducts();

            assertEquals(2, result.size());
            verify(productRepository).findAll();
        }

        @Test
        void testUpdateProduct_Success() {
            Long id = 1L;
            ProductCreateDTO dto = ProductFactory.productCreateDTO();
            Product existing = ProductFactory.product();
            Product updated = ProductFactory.product();
            ProductResponseDTO responseDTO = ProductFactory.productResponseDTO();

            when(productRepository.findById(id)).thenReturn(Optional.of(existing));
            when(productRepository.save(existing)).thenReturn(updated);
            when(productMapper.toResponseDTO(updated)).thenReturn(responseDTO);

            ProductResponseDTO result = productService.updateProduct(id, dto);

            assertNotNull(result);
            assertEquals(responseDTO, result);
            verify(productRepository).save(existing);
        }

    @Test
    void testUpdateProduct_NotFound() {
        Long id = 1L;
        ProductCreateDTO dto = ProductFactory.productCreateDTO();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(id, dto));
    }

    @Test
    void testDecreaseStock_Success() {
        Long id = 1L;
        int quantity = 2;
        Product product = ProductFactory.product();
        ProductResponseDTO responseDTO = ProductFactory.productResponseDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.decreaseStock(id, quantity)).thenReturn(1);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.decreaseStock(id, quantity);

        assertNotNull(result);
        verify(productRepository, times(2)).findById(id);
        verify(productRepository).decreaseStock(id, quantity);
    }

    @Test
    void testDecreaseStock_ProductNotFound() {
        Long id = 1L;
        int quantity = 2;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.decreaseStock(id, quantity));
    }

    @Test
    void testDecreaseStock_QuantityInvalid() {
        Long id = 1L;
        int quantity = 2;
        Product product = ProductFactory.product();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.decreaseStock(id, quantity)).thenReturn(0);

        assertThrows(ResourceBadRequestException.class, () -> productService.decreaseStock(id, quantity));
    }

    @Test
    void testDeleteProduct_Success() {
        Long id = 1L;
        doNothing().when(productRepository).deleteById(id);

        productService.deleteProduct(id);

        verify(productRepository).deleteById(id);
    }
}
