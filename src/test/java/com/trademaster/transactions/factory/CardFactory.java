package com.trademaster.transactions.factory;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.domain.enums.CardType;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.CardResponseDTO;

import java.math.BigDecimal;

public class CardFactory {

    public static CardCreateDTO cardCreateDTO() {
        return CardCreateDTO.builder()
                .cardNumber("1234567890")
                .type(CardType.CREDIT)
                .clientId(1L)
                .balance(BigDecimal.valueOf(99999))
                .build();
    }

    public static Card card() {
        return Card.builder()
                .cardNumber("1234567890")
                .type(CardType.CREDIT)
                .id(1L)
                .balance(BigDecimal.valueOf(99999))
                .build();
    }

    public static CardResponseDTO cardResponseDTO() {
        return CardResponseDTO.builder()
                .id(1L)
                .cardNumber("1234567890")
                .type(CardType.CREDIT)
                .balance(BigDecimal.valueOf(99999))
                .build();
    }
}
