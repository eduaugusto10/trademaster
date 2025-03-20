package com.trademaster.transactions.controller;


import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.model.TransactionCreateDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;
import com.trademaster.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionCreateDTO transactionCreateDTO) {
        TransactionResponseDTO transaction = transactionService.saveTransaction(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> findAllTransactions() {
        List<TransactionResponseDTO> transactions = transactionService.findAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        TransactionResponseDTO transaction = transactionService.findTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
}
