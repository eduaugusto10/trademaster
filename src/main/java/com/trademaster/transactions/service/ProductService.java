package com.trademaster.transactions.service;

import com.trademaster.transactions.model.ProductCreateDTO;
import com.trademaster.transactions.model.ProductResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductResponseDTO saveProduct(ProductCreateDTO productDTO);

    Optional<ProductResponseDTO> findProductById(Long id);

    List<ProductResponseDTO> findAllProducts();

    ProductResponseDTO updateProduct(Long id, ProductCreateDTO productDTO);

    void deleteProduct(Long id);

    ProductResponseDTO decreaseStock(Long id, Integer quantity);
}
