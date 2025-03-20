package com.trademaster.transactions.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCreateDTO {
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
