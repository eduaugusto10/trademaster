package com.trademaster.transactions.messaging;

import com.trademaster.transactions.domain.Client;
import com.trademaster.transactions.domain.Transaction;
import com.trademaster.transactions.domain.enums.TransactionStatus;
import com.trademaster.transactions.exception.ResourceBadRequestException;
import com.trademaster.transactions.exception.ResourceNotFoundException;
import com.trademaster.transactions.model.CardResponseDTO;
import com.trademaster.transactions.model.ProductResponseDTO;
import com.trademaster.transactions.model.TransactionResponseDTO;
import com.trademaster.transactions.service.CardService;
import com.trademaster.transactions.service.ClientService;
import com.trademaster.transactions.service.ProductService;
import com.trademaster.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionReceiver {

    private static final Logger logger = LoggerFactory.getLogger(TransactionReceiver.class);

    private final TransactionService transactionService;

    private final ClientService clientService;

    private final CardService cardService;

    private final ProductService productService;

    @Value("${thread.pool.size}")
    private int threadPoolSize;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${batch.size}")
    private int BATCH_SIZE;

    private List<Transaction> transactionBuffer = new ArrayList<>();

    @RabbitListener(queues = "${rabbitmq.queue.transactions.news}", containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(Transaction transaction) {
        logger.info("Recebida transação: {}", transaction);

        transactionBuffer.add(transaction);

        if (transactionBuffer.size() >= BATCH_SIZE) {
            processBatch(new ArrayList<>(transactionBuffer));
            transactionBuffer.clear();
        }
    }

    private void processBatch(List<Transaction> transactions) {
        logger.info("Processando lote de transações: {}", transactions);

        List<Future<?>> futures = new ArrayList<>();

        for (Transaction transaction : transactions) {
            Future<?> future = executorService.submit(() -> processTransaction(transaction.getId()));
            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.error("Erro no processamento da transação no batch", e);
            }
        }

        logger.info("Lote de transações processado com sucesso.");
    }

    public void processTransaction(Long transactionId) {
        try {
            log.info("Processando transação ID: {}", transactionId);

            TransactionResponseDTO transaction = transactionService.findTransactionById(transactionId);
            if (transaction == null) {
                log.warn("Transação não encontrada: {}", transactionId);
                return;
            }

            Client client = clientService.findClientEntityById(transaction.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

            CardResponseDTO card = cardService.findCardById(transaction.getCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado"));

            ProductResponseDTO product = productService.findProductById(transaction.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
            log.info("Verificando estoque: {} , quantidade do pedido: {}", product.getQuantity(), transaction.getQuantity());
            if (product.getQuantity() < transaction.getQuantity()) {
                throw new ResourceBadRequestException("Estoque insuficiente");
            }

            BigDecimal totalValue = product.getPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()));
            log.info("Verificando saldo atual: {} , valor do pedido: {} , saldo final: {}", card.getBalance(),product.getPrice(),totalValue);
            if (card.getBalance().compareTo(totalValue) < 0) {
                throw new ResourceBadRequestException("Saldo insuficiente");
            }

            var debit = cardService.debitBalance(card.getId(), totalValue);
            var stock = productService.decreaseStock(product.getId(), transaction.getQuantity());

            transactionService.updateTransaction(transaction.getId(), TransactionStatus.APROVADA);

            logger.info("Transação {} processada com sucesso", transaction.getId());

        } catch (Exception e) {
            logger.error("Erro ao processar transação ID: {}", transactionId, e);
        }
    }
}
