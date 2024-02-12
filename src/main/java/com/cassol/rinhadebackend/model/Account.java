package com.cassol.rinhadebackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    @Id
    private Long id;
    @Column(name = "account_limit")
    private Long limit;
    private Long balance;

}
