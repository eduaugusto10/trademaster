package com.trademaster.transactions.service.impl;

import com.trademaster.transactions.domain.Product;
import com.trademaster.transactions.domain.mapper.ProductMapper;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.model.ProductCreateDTO;
import com.trademaster.transactions.model.ProductResponseDTO;
import com.trademaster.transactions.repository.ProductRepository;
import com.trademaster.transactions.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDTO saveProduct(ProductCreateDTO productDTO) {
        log.info("Criando produto: {}",productDTO);
        Product product = productMapper.toEntity(productDTO);
        Product saved = productRepository.save(product);
        log.info("Produto criado: {}",saved);
        return productMapper.toResponseDTO(saved);
    }

    @Override
    public Optional<ProductResponseDTO> findProductById(Long id) {
        log.info("Buscando produto: {}",id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        return Optional.ofNullable(productMapper.toResponseDTO(product));
    }

    @Override
    public List<ProductResponseDTO> findAllProducts() {
        log.info("Buscando todos os produtos");
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductCreateDTO productDTO) {
        log.info("Atualizando produto: {}", productDTO);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        existing.setName(productDTO.getName());
        existing.setPrice(productDTO.getPrice());
        existing.setQuantity(productDTO.getQuantity());

        Product updated = productRepository.save(existing);
        log.info("Atualizado produto: {}", updated);
        return productMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public ProductResponseDTO decreaseStock(Long id, Integer quantity) {
        log.info("Alterando quantidade em estoque id: {}, quantidade: {}", id, quantity);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        int updatedRows = productRepository.decreaseStock(id, quantity);

        if (updatedRows == 0) {
            throw new ResourceBadRequestException(id.toString());
        }

        Product product = productRepository.findById(id).orElseThrow();
        log.info("Retirada de estoque: {}, estoque restante: {}", quantity, product.getQuantity());
        return productMapper.toResponseDTO(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deletando produto: {}", id);
        productRepository.deleteById(id);
    }
}
