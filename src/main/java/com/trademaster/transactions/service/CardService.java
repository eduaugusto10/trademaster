package com.trademaster.transactions.service;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.CardResponseDTO;

import java.math.BigDecimal;
import java.util.Optional;

public interface CardService {

    public Optional<CardResponseDTO>  findCardById(Long id);

    Optional<Card> findCardEntityById(Long id);

    CardResponseDTO saveCard(CardCreateDTO cardCreateDTO);

    CardResponseDTO updateCard(CardCreateDTO cardDTO);

    CardResponseDTO debitBalance(Long id, BigDecimal value);
}
