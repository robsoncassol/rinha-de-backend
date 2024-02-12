package com.cassol.rinhadebackend.repository;

import com.cassol.rinhadebackend.model.Account;
import com.cassol.rinhadebackend.model.AccountTransaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findTop10ByAccountOrderByCreateAtDesc(Account account);

}
