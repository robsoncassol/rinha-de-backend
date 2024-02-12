package com.cassol.rinhadebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.model.Account;
import com.cassol.rinhadebackend.repository.AccountRepository;
import com.cassol.rinhadebackend.repository.AccountTransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class AccountTransactionServiceTest {

    @InjectMocks
    private AccountTransactionService accountTransactionService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountTransactionRepository accountTransactionRepository;

    @Test
    void testCreditTransaction() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.ofNullable(Account.builder().id(1L).balance(0L).limit(10000L).build()));
        TransactionResult result = accountTransactionService.transaction(1L, 1000L, "C", "description");
        assertNotNull(result);
        assertEquals(1000L, result.getBalance());
    }


    @Test
    void testDebitTransaction() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.ofNullable(Account.builder().id(1L).balance(0L).limit(10000L).build()));
        TransactionResult result = accountTransactionService.transaction(1L, 1000L, "D", "description");
        assertNotNull(result);
        assertEquals(-1000L, result.getBalance());
    }

    @Test
    void testDebitTransactionAboveLimit() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.ofNullable(Account.builder().id(1L).balance(0L).limit(10000L).build()));
        String message = assertThrows(IllegalArgumentException.class, () -> accountTransactionService.transaction(1L, 11000L, "D", "description"))
            .getMessage();
        assertEquals("Insufficient funds", message);
    }

}