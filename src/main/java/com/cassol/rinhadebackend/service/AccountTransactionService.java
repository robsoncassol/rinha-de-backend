package com.cassol.rinhadebackend.service;

import com.cassol.rinhadebackend.dto.BalanceView;
import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.dto.TransactionView;
import com.cassol.rinhadebackend.model.Account;
import com.cassol.rinhadebackend.model.AccountTransaction;
import com.cassol.rinhadebackend.repository.AccountRepository;
import com.cassol.rinhadebackend.repository.AccountTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionResult transaction(Long accountId, Long amount, String type, String description) {
        Optional<Account> accountOp = accountRepository.findById(accountId);
        if (accountOp.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        Account account = accountOp.get();
        account.setBalance(computeBalance(amount, type, account));
        publishNewTransaction(amount, type, description, account);
        return TransactionResult.builder()
            .balance(account.getBalance())
            .limit(account.getLimit())
            .build();
    }

    private void publishNewTransaction(Long amount, String type, String description, Account account) {
        accountTransactionRepository.save(AccountTransaction.builder()
            .account(account)
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
            Long balance = account.getBalance() - amount;
            if (balance < 0 && Math.abs(balance) > account.getLimit()) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            return balance;
        }
        throw new IllegalArgumentException("Invalid transaction type");
    }

    public Statement statement(Long accountId) {
        Optional<Account> accountOp = accountRepository.findById(accountId);
        if (accountOp.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        Account account = accountOp.get();
        List<TransactionView> transactions = accountTransactionRepository.findTop10ByAccountOrderByCreateAtDesc(account)
            .stream()
            .map(t -> TransactionView.builder()
                .date(t.getCreateAt())
                .amount(t.getAmount())
                .description(t.getDescription())
                .type(t.getType())
                .build())
            .toList();
        return Statement.builder()
            .balance(BalanceView.builder()
                .amount(account.getBalance())
                .date(LocalDateTime.now())
                .limit(account.getLimit())
                .build())
            .transactions(transactions)
            .build();
    }
}
