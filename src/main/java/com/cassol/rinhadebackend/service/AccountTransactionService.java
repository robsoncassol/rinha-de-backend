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
import com.cassol.rinhadebackend.transaction.TransactionTaskRunner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@AllArgsConstructor
@Log4j2
public class AccountTransactionService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final TransactionAsyncProcessor transactionAsyncProcessor;
    private final TransactionTaskRunner transactionTaskRunner;

    @Transactional(propagation = Propagation.NEVER)
    public TransactionResult transaction(Long accountId, Long amount, String type, String description) {
        Account modiedAccount = transactionTaskRunner.readWriteAndGet(() -> {
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
            account.setBalance(computeBalance(amount, type, account));
            accountRepository.save(account);
            accountTransactionRepository.save(AccountTransaction.builder()
                .uuid(UUID.randomUUID())
                .description(description)
                .accountId(accountId)
                .amount(amount)
                .createAt(LocalDateTime.now())
                .type(type)
                .build());
            return account;
        });
        return TransactionResult.builder()
            .saldo(modiedAccount.getBalance())
            .limite(modiedAccount.getLimit())
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

    public Statement statement(Long accountId) {
        return transactionTaskRunner.readOnlyAndGet(() -> {
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
        });
    }
}
