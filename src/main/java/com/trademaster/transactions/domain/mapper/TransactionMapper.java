package com.trademaster.transactions.domain.mapper;

import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.model.TransactionCreateDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "productId",target = "productId")
    Transaction toEntity(TransactionCreateDTO dto);

    @Mapping(source = "cardId", target = "cardId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "quantity", target = "quantity")
    TransactionResponseDTO toResponseDTO(Transaction transaction);
}

