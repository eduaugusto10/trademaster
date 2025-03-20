package com.trademaster.transactions.service;


import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.domain.enums.TransactionStatus;
import com.trademaster.transactions.domain.mapper.TransactionMapper;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.factory.CardFactory;
import com.trademaster.transactions.factory.ClientFactory;
import com.trademaster.transactions.factory.TransactionFactory;
import com.trademaster.transactions.model.TransactionCreateDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;
import com.trademaster.transactions.repository.TransactionRepository;
import com.trademaster.transactions.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(
                transactionRepository, clientService, transactionMapper, rabbitTemplate
        );
        ReflectionTestUtils.setField(transactionService, "transactionsExchange", "transaction-exchange");
        ReflectionTestUtils.setField(transactionService, "transactionsNewsRoutingKey", "transaction-routing");
    }

    @Test
    void testSaveTransaction_Success() {
        TransactionCreateDTO createDTO = TransactionFactory.transactionCreateDTO();
        Transaction transactionEntity = TransactionFactory.transaction();
        transactionEntity.setId(1L);

        Transaction savedTransaction = TransactionFactory.transaction();
        savedTransaction.setId(1L);
        savedTransaction.setStatus(TransactionStatus.PENDENTE);

        TransactionResponseDTO responseDTO = TransactionFactory.transactionResponseDTO();

        Client client = ClientFactory.client();
        client.setId(1L);
        client.setCards(Collections.singletonList(CardFactory.card()));

        when(clientService.findClientEntityById(1L)).thenReturn(Optional.of(client));
        when(transactionMapper.toEntity(createDTO)).thenReturn(transactionEntity);
        when(transactionRepository.save(transactionEntity)).thenReturn(savedTransaction);
        when(transactionMapper.toResponseDTO(savedTransaction)).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.saveTransaction(createDTO);

        assertNotNull(result);
        verify(transactionRepository).save(transactionEntity);
        verify(rabbitTemplate).convertAndSend("transaction-exchange", "transaction-routing", savedTransaction);
    }

    @Test
    void testSaveTransaction_ClientNotFound() {
        TransactionCreateDTO createDTO = TransactionFactory.transactionCreateDTO();
        when(clientService.findClientEntityById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.saveTransaction(createDTO));
    }

    @Test
    void testFindTransactionById_Success() {
        Transaction transaction = TransactionFactory.transaction();
        transaction.setId(1L);
        TransactionResponseDTO responseDTO = TransactionFactory.transactionResponseDTO();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toResponseDTO(transaction)).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.findTransactionById(1L);

        assertNotNull(result);
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testFindTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findTransactionById(1L));
    }

    @Test
    void testFindAllTransactions_Success() {
        Transaction transaction1 = TransactionFactory.transaction();
        Transaction transaction2 = TransactionFactory.transaction();

        TransactionResponseDTO responseDTO1 = TransactionFactory.transactionResponseDTO();
        TransactionResponseDTO responseDTO2 = TransactionFactory.transactionResponseDTO();

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toResponseDTO(transaction1)).thenReturn(responseDTO1);
        when(transactionMapper.toResponseDTO(transaction2)).thenReturn(responseDTO2);

        List<TransactionResponseDTO> result = transactionService.findAllTransactions();

        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }

    @Test
    void testUpdateTransaction_Success() {
        Transaction transaction = TransactionFactory.transaction();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.PENDENTE);

        Transaction updatedTransaction = TransactionFactory.transaction();
        updatedTransaction.setId(1L);
        updatedTransaction.setStatus(TransactionStatus.APROVADA);

        TransactionResponseDTO responseDTO = TransactionFactory.transactionResponseDTO();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(updatedTransaction);
        when(transactionMapper.toResponseDTO(updatedTransaction)).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.updateTransaction(1L, TransactionStatus.APROVADA);

        assertNotNull(result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void testUpdateTransaction_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(1L, TransactionStatus.APROVADA));
    }
}