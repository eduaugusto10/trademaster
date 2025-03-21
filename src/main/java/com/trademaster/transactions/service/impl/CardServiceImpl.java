package com.trademaster.transactions.service.impl;

import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.mapper.CardMapper;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.CardResponseDTO;
import com.trademaster.transactions.repository.CardRepository;
import com.trademaster.transactions.repository.ClientRepository;
import com.trademaster.transactions.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final ClientRepository clientRepository;

    private final CardMapper cardMapper;

    @Override
    @Transactional
    public CardResponseDTO saveCard(CardCreateDTO cardDto) {
        log.info("Criando cartão: {}", cardDto);
        Client client = clientRepository.findById(cardDto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        Card savedCard = cardRepository.save(cardMapper.toEntity(cardDto));
        log.info("Cartão criado: {}", savedCard);
        return cardMapper.toResponseDTO(savedCard);
    }

    @Override
    public Optional<CardResponseDTO> findCardById(Long id) {
        log.info("Buscando cartão: {}", id);
        return cardRepository.findById(id)
                .map(cardMapper::toResponseDTO);
    }


    @Override
    public Optional<Card> findCardEntityById(Long id) {
        log.info("Buscando cartão: {}", id);
        return cardRepository.findById(id);
    }

    @Override
    @Transactional
    public CardResponseDTO updateCard(CardCreateDTO cardDTO) {
        log.info("Atualizando cartão: {}", cardDTO);
        Card existingCard = cardRepository.findById(cardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado"));

        clientRepository.findById(cardDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        existingCard.setCardNumber(cardDTO.getCardNumber());
        existingCard.setType(cardDTO.getType());
        existingCard.setBalance(cardDTO.getBalance());

        Card updatedCard = cardRepository.save(existingCard);
        log.info("Cartão atualizado: {}", updatedCard);
        return cardMapper.toResponseDTO(updatedCard);
    }

    @Transactional
    public CardResponseDTO debitBalance(Long id, BigDecimal value) {
        log.info("Atualizando saldo cliente: {} valor: {}", id, value);
        Card updatedCard = updateDebit(id, value);
        log.info("Valor debitado: {}, saldo restante: {}", value, updatedCard.getBalance());
        return cardMapper.toResponseDTO(updatedCard);
    }

    private Card updateDebit(Long id, BigDecimal value) {
        int updatedRows = cardRepository.debitCardBalance(id, value);

        if (updatedRows == 0) {
            throw new ResourceBadRequestException("Saldo insuficiente para débito");
        }
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado"));
    }
}
