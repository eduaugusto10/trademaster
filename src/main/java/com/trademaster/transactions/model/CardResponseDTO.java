package com.trademaster.transactions.model;

import com.trademaster.transactions.domain.enums.CardType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CardResponseDTO {
    private Long id;
    private String cardNumber;
    private CardType type;
    private BigDecimal balance;
}
