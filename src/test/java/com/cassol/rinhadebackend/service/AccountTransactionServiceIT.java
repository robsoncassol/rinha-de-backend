package com.cassol.rinhadebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionResult;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AccountTransactionServiceIT {

    @Autowired
    private AccountTransactionService accountTransactionService;

    @Test
    void testCreditTransaction() {
        TransactionResult transaction = accountTransactionService.transaction(1L, 1000L, "C", "description");
        assertEquals(1000L, transaction.getBalance());
        transaction = accountTransactionService.transaction(1L, 1000L, "C", "description");
        assertEquals(2000L, transaction.getBalance());
    }


    @Test
    void testDebitTransaction() {
        TransactionResult transaction = accountTransactionService.transaction(2L, 1000L, "D", "description");
        assertEquals(-1000L, transaction.getBalance());
        transaction = accountTransactionService.transaction(2L, 1000L, "D", "description");
        assertEquals(-2000L, transaction.getBalance());
    }

    @Test
    void testDebitTransactionAboveLimit() {
        accountTransactionService.transaction(2L, 80000L, "D", "description");
        String message = assertThrows(IllegalArgumentException.class, () -> accountTransactionService.transaction(2L, 1L, "D", "description"))
            .getMessage();
        assertEquals("Insufficient funds", message);
        Statement statement = accountTransactionService.statement(2L);
        assertEquals(-80000L, statement.getBalance().getAmount());
        assertEquals(1, statement.getTransactions().size());
    }

}