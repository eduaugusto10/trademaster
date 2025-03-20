package com.trademaster.transactions.service;

import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.domain.enums.TransactionStatus;
import com.trademaster.transactions.model.TransactionCreateDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO saveTransaction(TransactionCreateDTO transactionCreateDTO);

    TransactionResponseDTO findTransactionById(Long id);

    List<TransactionResponseDTO> findAllTransactions();

    TransactionResponseDTO updateTransaction(Long id, TransactionStatus status);
}
