package com.trademaster.transactions.domain.mapper;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.CardResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toEntity(CardCreateDTO dto);
    CardResponseDTO toResponseDTO(Card card);
}
