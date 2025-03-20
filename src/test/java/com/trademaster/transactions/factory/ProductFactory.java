package com.trademaster.transactions.factory;

import com.trademaster.transactions.domain.Product;
import com.trademaster.transactions.model.ProductCreateDTO;
import com.trademaster.transactions.model.ProductResponseDTO;

import java.math.BigDecimal;

public class ProductFactory {

    public static ProductCreateDTO productCreateDTO(){
        return ProductCreateDTO.builder()
                .name("TV Samsung 21")
                .price(BigDecimal.TEN)
                .quantity(100)
                .build();
    }

    public static Product product(){
        return Product.builder()
                .id(1L)
                .name("TV Samsung 21")
                .price(BigDecimal.TEN)
                .quantity(100)
                .build();
    }
    public static ProductResponseDTO productResponseDTO(){
        return ProductResponseDTO.builder()
                .id(1L)
                .name("TV Samsung 21")
                .price(BigDecimal.TEN)
                .quantity(100)
                .build();
    }

}
