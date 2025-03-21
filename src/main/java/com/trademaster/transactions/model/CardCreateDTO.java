package com.trademaster.transactions.model;

import com.trademaster.transactions.domain.enums.CardType;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CardCreateDTO {
    private Long id;

    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}",
            message = "O número do cartão deve estar no formato 1234-5678-9876-3333")
    private String cardNumber;
    private CardType type;
    private BigDecimal balance;
    private Long clientId;
}
