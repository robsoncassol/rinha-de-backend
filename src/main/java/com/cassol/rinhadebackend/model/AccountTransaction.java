package com.cassol.rinhadebackend.model;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransaction {

    @Id
    @GeneratedValue
    private Long id;
    private TransactionOperation type;
    private String description;
    private Long amount;
    @CreatedDate
    private LocalDateTime createAt;
    @ManyToOne
    private Account account;

}
