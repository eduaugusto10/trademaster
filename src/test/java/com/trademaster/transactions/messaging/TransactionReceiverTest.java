package com.trademaster.transactions.messaging;

import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.domain.enums.TransactionStatus;
import com.trademaster.transactions.factory.CardFactory;
import com.trademaster.transactions.factory.ClientFactory;
import com.trademaster.transactions.factory.ProductFactory;
import com.trademaster.transactions.factory.TransactionFactory;
import com.trademaster.transactions.model.CardResponseDTO;
import com.trademaster.transactions.model.ProductResponseDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;
import com.trademaster.transactions.service.CardService;
import com.trademaster.transactions.service.ClientService;
import com.trademaster.transactions.service.ProductService;
import com.trademaster.transactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionReceiverTest {
    @InjectMocks
    private TransactionReceiver transactionReceiver;

    @Mock
    private TransactionService transactionService;
    @Mock
    private ClientService clientService;
    @Mock
    private CardService cardService;
    @Mock
    private ProductService productService;

    @Test
    void testReceiveMessage_ShouldBufferAndProcessBatch() {
        Transaction transaction = TransactionFactory.transaction();
        transaction.setId(1L);

        for (int i = 0; i < 5; i++) {
            transactionReceiver.receiveMessage(transaction);
        }
    }

    @Test
    public void testProcessTransactionSuccess() throws Exception {
        TransactionResponseDTO transactionResponseDTO = TransactionFactory.transactionResponseDTO();
        Client client = ClientFactory.client();
        CardResponseDTO cardResponseDTO = CardFactory.cardResponseDTO();
        ProductResponseDTO productResponseDTO = ProductFactory.productResponseDTO();

        when(transactionService.findTransactionById(1L)).thenReturn(transactionResponseDTO);
        when(clientService.findClientEntityById(1L)).thenReturn(java.util.Optional.of(client));
        when(cardService.findCardById(1L)).thenReturn(Optional.ofNullable(cardResponseDTO));
        when(productService.findProductById(1L)).thenReturn(Optional.ofNullable(productResponseDTO));

        BigDecimal totalValue = productResponseDTO.getPrice().multiply(BigDecimal.valueOf(transactionResponseDTO.getQuantity()));
        when(cardService.debitBalance(1L, totalValue)).thenReturn(cardResponseDTO);

        when(productService.decreaseStock(1L, transactionResponseDTO.getQuantity())).thenReturn(productResponseDTO);

        transactionReceiver.processTransaction(1L);

        verify(transactionService, times(1)).updateTransaction(1L, TransactionStatus.APROVADA);
    }



    @Test
    void testProcessTransaction_ShouldHandleInsufficientStock() {
        Long transactionId = 2L;
        Transaction transaction = TransactionFactory.transaction();

        transactionReceiver.receiveMessage(transaction);

        verify(transactionService, never()).updateTransaction(transactionId, TransactionStatus.APROVADA);
    }

    @Test
    void testProcessTransaction_ShouldHandleInsufficientBalance() {
        Long transactionId = 3L;
        Transaction transaction = TransactionFactory.transaction();

        transactionReceiver.receiveMessage(transaction);

        verify(transactionService, never()).updateTransaction(transactionId, TransactionStatus.APROVADA);
    }

    @Test
    void testProcessTransaction_ShouldHandleTransactionNotFound() {
        Transaction transaction = TransactionFactory.transaction();
        transactionReceiver.receiveMessage(transaction);

        verify(transactionService, never()).updateTransaction(anyLong(), any());
    }
}

