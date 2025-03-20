package com.trademaster.transactions.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionCreateDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Long cardId;
    private BigDecimal value;
    private Long clientId;
    private String description;
    private LocalDateTime dateTime;

}
