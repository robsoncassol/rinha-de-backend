package com.cassol.rinhadebackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionView {

    private String descricao;
    private Long valor;
    private String tipo;
    private LocalDateTime realizada_em;



}
