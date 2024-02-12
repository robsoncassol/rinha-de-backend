package com.cassol.rinhadebackend.service;

import com.cassol.rinhadebackend.dto.BalanceView;
import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.dto.TransactionView;
import com.cassol.rinhadebackend.exceptions.BusinessRuleException;
import com.cassol.rinhadebackend.exceptions.EntityNotFoundException;
import com.cassol.rinhadebackend.model.Account;
import com.cassol.rinhadebackend.model.AccountTransaction;
import com.cassol.rinhadebackend.model.TransactionOperation;
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
    public TransactionResult transaction(Long accountId, Long amount, TransactionOperation type, String description) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));

        Long newBalance = computeBalance(amount, type, account);
        account.setBalance(newBalance);
        publishNewTransactionEvent(amount, type, description, account);

        return TransactionResult.builder()
            .balance(account.getBalance())
            .limit(account.getLimit())
            .build();
    }

    private void publishNewTransactionEvent(Long amount, TransactionOperation type, String description, Account account) {
        accountTransactionRepository.save(AccountTransaction.builder()
            .account(account)
            .description(description)
            .type(type)
            .amount(amount)
            .build());
    }

    private Long computeBalance(Long amount, TransactionOperation type, Account account) {
        if (TransactionOperation.CREDIT == type) {
            return account.getBalance() + amount;
        }
        if (TransactionOperation.DEBIT == type) {
            Long balance = account.getBalance() - amount;
            if (balance < 0 && Math.abs(balance) > account.getLimit()) {
                throw new BusinessRuleException("Insufficient funds");
            }
            return balance;
        }
        return account.getBalance();
    }

    public Statement statement(Long accountId) {
        Optional<Account> accountOp = accountRepository.findById(accountId);
        if (accountOp.isEmpty()) {
            throw new EntityNotFoundException(Account.class, accountId);
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
