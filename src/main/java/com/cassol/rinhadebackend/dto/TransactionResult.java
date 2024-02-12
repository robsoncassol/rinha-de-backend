package com.cassol.rinhadebackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResult {

    @JsonAlias("limite")
    private Long limit;
    @JsonAlias("saldo")
    private Long balance;

}
