package com.cassol.rinhadebackend.repository;

import com.cassol.rinhadebackend.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
