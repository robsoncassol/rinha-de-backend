package com.cassol.rinhadebackend.service;

import com.cassol.rinhadebackend.dto.BalanceView;
import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.dto.TransactionView;
import com.cassol.rinhadebackend.exceptions.BusinessRuleException;
import com.cassol.rinhadebackend.exceptions.EntityNotFoundException;
import com.cassol.rinhadebackend.model.Account;
import com.cassol.rinhadebackend.model.AccountTransaction;
import com.cassol.rinhadebackend.repository.AccountRepository;
import com.cassol.rinhadebackend.repository.AccountTransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class AccountTransactionService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    @Transactional
    public TransactionResult transaction(Long accountId, Long amount, String type, String description) {
        Account account = accountRepository.findByIdAndLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
        account.setBalance(computeBalance(amount, type, account));
        accountRepository.save(account);
        accountTransactionRepository.save(AccountTransaction.builder()
                .description(description)
                .accountId(accountId)
                .amount(amount)
                .createAt(LocalDateTime.now())
                .type(type)
                .build());
        return TransactionResult.builder()
                .saldo(account.getBalance())
                .limite(account.getLimit())
                .build();
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

    @Transactional(readOnly = true)
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
