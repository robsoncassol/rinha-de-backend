package com.cassol.rinhadebackend.repository;

import com.cassol.rinhadebackend.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface AccountRepository extends JpaRepository<Account, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM com.cassol.rinhadebackend.model.Account c WHERE c.id = ?1")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})
    Optional<Account> findByIdAndLock(Long accountId);
}
