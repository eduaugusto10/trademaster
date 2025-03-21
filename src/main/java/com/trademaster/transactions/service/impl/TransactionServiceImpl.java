package com.trademaster.transactions.service.impl;

import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.domain.enums.TransactionStatus;
import com.trademaster.transactions.domain.mapper.TransactionMapper;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.model.TransactionCreateDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;
import com.trademaster.transactions.repository.TransactionRepository;
import com.trademaster.transactions.service.ClientService;
import com.trademaster.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final ClientService clientService;

    private final TransactionMapper transactionMapper;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.transactions}")
    private String transactionsExchange;

    @Value("${rabbitmq.routingkey.transactions.news}")
    private String transactionsNewsRoutingKey;

    @Override
    @Transactional
    public TransactionResponseDTO saveTransaction(TransactionCreateDTO transactionCreateDTO) {
        log.info("Criando transação: {}", transactionCreateDTO);
        validatedClient(transactionCreateDTO);

        Transaction savedTransaction = saveAndSend(transactionCreateDTO);
        return transactionMapper.toResponseDTO(savedTransaction);
    }

    @Override
    public TransactionResponseDTO findTransactionById(Long id) {
        log.info("Buscando transação: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));
        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> findAllTransactions() {
        log.info("Buscando todas as transações");
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(transactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionStatus status) {
        log.info("Atualizando status da transação, id: {}, status:{}", id, status);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        transaction.setStatus(status);
        transaction = transactionRepository.save(transaction);
        log.info("Transação atualizada: {}", transaction);
        return transactionMapper.toResponseDTO(transaction);
    }

    private void sendTransactionToQueue(Transaction transaction) {
        log.info("Enviando transação: {} para a fila", transaction);
        try {
            rabbitTemplate.convertAndSend(
                    transactionsExchange,
                    transactionsNewsRoutingKey,
                    transaction
            );
            log.info("Transação com ID {} enviada para a fila.", transaction.getId());
        } catch (AmqpException e) {
            log.error("Falha ao enviar transação {} para a fila: {}",
                    transaction.getId(), e.getMessage());
        }
    }

    private void validatedClient(TransactionCreateDTO transactionCreateDTO) {
        Client client = clientService.findClientEntityById(transactionCreateDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        boolean cardExists = client.getCards().stream()
                .anyMatch(card -> card.getId().equals(transactionCreateDTO.getCardId()));

        if (!cardExists) {
            throw new ResourceNotFoundException("Cartão não encontrado para o cliente");
        }
    }

    private Transaction saveAndSend(TransactionCreateDTO transactionCreateDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionCreateDTO);
        transaction.setStatus(TransactionStatus.PENDENTE);
        var savedTransaction = transactionRepository.save(transaction);
        sendTransactionToQueue(savedTransaction);
        log.info("Transação criada: {}", savedTransaction);
        return savedTransaction;
    }
}
