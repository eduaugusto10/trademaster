package com.trademaster.transactions.domain.mapper;

import com.trademaster.transactions.domain.Product;
import com.trademaster.transactions.model.ProductCreateDTO;
import com.trademaster.transactions.model.ProductResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductCreateDTO dto);
    ProductResponseDTO toResponseDTO(Product product);
}
