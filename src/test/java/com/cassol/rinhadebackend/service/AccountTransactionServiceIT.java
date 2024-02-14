package com.cassol.rinhadebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.exceptions.BusinessRuleException;

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
        assertEquals(1000L, transaction.getSaldo());
        transaction = accountTransactionService.transaction(1L, 1000L, "C", "description");
        assertEquals(2000L, transaction.getSaldo());
    }

    @Test
    void testDebitTransaction() {
        TransactionResult transaction = accountTransactionService.transaction(3L, 1000L, "D", "description");
        assertEquals(-1000L, transaction.getSaldo());
        transaction = accountTransactionService.transaction(3L, 1000L, "D", "description");
        assertEquals(-2000L, transaction.getSaldo());
    }

    @Test
    void testCreditAndDebitTransaction() {
        TransactionResult transaction = accountTransactionService.transaction(4L, 1000L, "C", "description");
        assertEquals(1000L, transaction.getSaldo());
        transaction = accountTransactionService.transaction(4L, 1000L, "D", "description");
        assertEquals(0L, transaction.getSaldo());
    }

    @Test
    void testDebitTransactionAboveLimit() throws InterruptedException {
        accountTransactionService.transaction(2L, 80000L, "D", "description");
        String message = assertThrows(BusinessRuleException.class, () -> accountTransactionService.transaction(2L, 1L, "D", "description"))
            .getMessage();
        assertEquals("Insufficient funds", message);
        Statement statement = accountTransactionService.statement(2L);
        assertEquals(-80000L, statement.getSaldo().getTotal());
        assertEquals(1, statement.getUltimas_transacoes().size());
    }

}