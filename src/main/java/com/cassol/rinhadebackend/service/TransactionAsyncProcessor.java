package com.cassol.rinhadebackend.service;

import com.cassol.rinhadebackend.dto.NewTransactionEvent;
import com.cassol.rinhadebackend.model.AccountTransaction;
import com.cassol.rinhadebackend.repository.AccountTransactionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransactionAsyncProcessor {

    private final LinkedBlockingQueue<NewTransactionEvent> messageQueue = new LinkedBlockingQueue<>();
    private final AccountTransactionRepository accountTransactionRepository;

    public TransactionAsyncProcessor(AccountTransactionRepository accountTransactionRepository) {
        this.accountTransactionRepository = accountTransactionRepository;
//        startProcessing();
    }

    public void sendMessage(NewTransactionEvent message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void startProcessing() {
        log.debug("Starting transaction processing");
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    NewTransactionEvent event = messageQueue.take();
                    processMessage(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void processMessage(NewTransactionEvent event) {
        accountTransactionRepository.save(AccountTransaction.builder()
            .uuid(UUID.randomUUID())
            .description(event.getDescription())
            .amount(event.getAmount())
            .type(event.getType())
            .accountId(event.getAccountId())
            .createAt(LocalDateTime.now())
            .build());
    }
}
