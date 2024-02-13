package com.cassol.rinhadebackend.service;

import com.cassol.rinhadebackend.dto.BalanceView;
import com.cassol.rinhadebackend.dto.NewTransactionEvent;
import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.dto.TransactionView;
import com.cassol.rinhadebackend.exceptions.BusinessRuleException;
import com.cassol.rinhadebackend.exceptions.EntityNotFoundException;
import com.cassol.rinhadebackend.model.Account;
import com.cassol.rinhadebackend.model.AccountTransaction;
import com.cassol.rinhadebackend.repository.AccountRepository;
import com.cassol.rinhadebackend.repository.AccountTransactionRepository;

import org.hibernate.StaleStateException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountTransactionService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final TransactionAsyncProcessor transactionAsyncProcessor;

    @Retryable(maxAttempts = 5, include = StaleStateException.class)
    @Transactional
    public TransactionResult transaction(Long accountId, Long amount, String type, String description) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));

        Long newBalance = computeBalance(amount, type, account);
        account.setBalance(newBalance);
        accountTransactionRepository.save(AccountTransaction.builder()
                .description(description)
                .accountId(account.getId())
                .amount(amount)
                .type(type)
            .build());

        return TransactionResult.builder()
            .saldo(account.getBalance())
            .limite(account.getLimit())
            .build();
    }

    private void publishNewTransactionEvent(Long amount, String type, String description, Account account) {
        transactionAsyncProcessor.sendMessage(NewTransactionEvent.builder()
            .accountId(account.getId())
            .description(description)
            .type(type)
            .amount(amount)
            .build());
    }

    private Long computeBalance(Long amount, String type, Account account) {
        if ("C".equalsIgnoreCase(type)) {
            return account.getBalance() + amount;
        }
        if ("D".equalsIgnoreCase(type)) {
            Long newBalance = account.getBalance() - amount;
            if (newBalance < 0 && (Math.abs(newBalance) > account.getLimit())) {
                throw new BusinessRuleException("Insufficient funds");
            }
            return newBalance;
        }
        throw new BusinessRuleException("Invalid transaction type");
    }

    @Transactional
    public Statement statement(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
        List<TransactionView> transactions = accountTransactionRepository.findTop10ByAccountIdOrderByCreateAtDesc(account.getId())
            .stream()
            .map(t -> TransactionView.builder()
                .realizada_em(t.getCreateAt())
                .valor(t.getAmount())
                .descricao(t.getDescription())
                .tipo(t.getType())
                .build())
            .toList();
        return Statement.builder()
            .saldo(BalanceView.builder()
                .total(account.getBalance())
                .data_extrato(LocalDateTime.now())
                .limite(account.getLimit())
                .build())
            .ultimas_transacoes(transactions)
            .build();
    }
}
