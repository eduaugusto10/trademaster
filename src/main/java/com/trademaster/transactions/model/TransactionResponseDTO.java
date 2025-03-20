package com.trademaster.transactions.model;

import com.trademaster.transactions.domain.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {

    private Long id;
    private BigDecimal value;
    private LocalDateTime dateTime;
    private TransactionStatus status;
    private String description;
    private Long cardId;
    private Long clientId;
    private Integer quantity;
    private Long productId;
}
