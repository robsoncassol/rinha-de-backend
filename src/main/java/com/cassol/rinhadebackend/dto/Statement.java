package com.cassol.rinhadebackend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Statement {

    private BalanceView saldo;
    private List<TransactionView> ultimas_transacoes;

}
