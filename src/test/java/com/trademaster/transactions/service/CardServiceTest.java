package com.trademaster.transactions.service;


import com.trademaster.transactions.domain.Card;
import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.mapper.CardMapper;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.factory.CardFactory;
import com.trademaster.transactions.factory.ClientFactory;
import com.trademaster.transactions.model.CardCreateDTO;
import com.trademaster.transactions.model.CardResponseDTO;
import com.trademaster.transactions.repository.CardRepository;
import com.trademaster.transactions.repository.ClientRepository;
import com.trademaster.transactions.service.impl.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CardMapper cardMapper;

    @Test
    void testSaveCard_Success() {
        CardCreateDTO dto = CardFactory.cardCreateDTO();
        Client client = ClientFactory.client();
        client.setId(1L);
        Card card = CardFactory.card();

        CardResponseDTO responseDTO = CardFactory.cardResponseDTO();

        when(clientRepository.findById(dto.getClientId())).thenReturn(Optional.of(client));
        when(cardMapper.toEntity(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toResponseDTO(card)).thenReturn(responseDTO);

        CardResponseDTO result = cardService.saveCard(dto);

        assertNotNull(result);
        verify(cardRepository).save(card);
    }

    @Test
    void testSaveCard_ClientNotFound() {
        CardCreateDTO dto = CardFactory.cardCreateDTO();
        when(clientRepository.findById(dto.getClientId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.saveCard(dto));
    }

    @Test
    void testFindCardById_Success() {
        Card card = CardFactory.card();
        CardResponseDTO responseDTO = CardFactory.cardResponseDTO();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toResponseDTO(card)).thenReturn(responseDTO);

        Optional<CardResponseDTO> result = cardService.findCardById(1L);

        assertTrue(result.isPresent());
        verify(cardRepository).findById(1L);
    }

    @Test
    void testUpdateCard_Success() {
        CardCreateDTO dto = CardFactory.cardCreateDTO();
        Client client = ClientFactory.client();
        Card existingCard = CardFactory.card();

        CardResponseDTO responseDTO = CardFactory.cardResponseDTO();

        when(cardRepository.findById(dto.getId())).thenReturn(Optional.of(existingCard));
        when(clientRepository.findById(dto.getClientId())).thenReturn(Optional.of(client));
        when(cardRepository.save(existingCard)).thenReturn(existingCard);
        when(cardMapper.toResponseDTO(existingCard)).thenReturn(responseDTO);

        CardResponseDTO result = cardService.updateCard(dto);

        assertNotNull(result);
        verify(cardRepository).save(existingCard);
    }

    @Test
    void testUpdateCard_CardNotFound() {
        CardCreateDTO dto = CardFactory.cardCreateDTO();
        when(cardRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.updateCard(dto));
    }

    @Test
    void testDebitBalance_Success() {
        Long cardId = 1L;
        BigDecimal debitAmount = BigDecimal.valueOf(10);
        Card card = CardFactory.card();
        card.setBalance(BigDecimal.valueOf(90));
        CardResponseDTO responseDTO = CardFactory.cardResponseDTO();

        when(cardRepository.debitCardBalance(cardId, debitAmount)).thenReturn(1);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponseDTO(card)).thenReturn(responseDTO);

        CardResponseDTO result = cardService.debitBalance(cardId, debitAmount);

        assertNotNull(result);
        verify(cardRepository).debitCardBalance(cardId, debitAmount);
    }

    @Test
    void testDebitBalance_InsufficientBalance() {
        Long cardId = 1L;
        BigDecimal debitAmount = BigDecimal.valueOf(100);

        when(cardRepository.debitCardBalance(cardId, debitAmount)).thenReturn(0);

        assertThrows(ResourceBadRequestException.class, () -> cardService.debitBalance(cardId, debitAmount));
    }

}

