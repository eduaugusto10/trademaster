package com.trademaster.transactions.factory;

import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.domain.enums.TransactionStatus;
import com.trademaster.transactions.model.TransactionCreateDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionFactory {

    public static TransactionCreateDTO transactionCreateDTO(){
        return TransactionCreateDTO.builder()
                .value(BigDecimal.valueOf(500.00))
                .dateTime(LocalDateTime.now())
                .description("Compra aprovada")
                .cardId(1L)
                .clientId(1L)
                .quantity(1)
                .productId(1L)
                .build();
    }
    public static TransactionResponseDTO transactionResponseDTO() {
        return TransactionResponseDTO.builder()
                .id(1L)
                .value(BigDecimal.valueOf(500.00))
                .dateTime(LocalDateTime.now())
                .status(TransactionStatus.APROVADA)
                .description("Compra aprovada")
                .cardId(1L)
                .clientId(1L)
                .quantity(1)
                .productId(1L)
                .build();
    }

    public static Transaction transaction(){
        return Transaction.builder()
                .id(1L)
                .value(BigDecimal.valueOf(500.00))
                .dateTime(LocalDateTime.now())
                .status(TransactionStatus.APROVADA)
                .description("Compra aprovada")
                .cardId(1L)
                .clientId(1L)
                .quantity(1)
                .productId(1L)
                .build();
    }
}
