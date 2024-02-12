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

    @JsonAlias("descricao")
    private String description;
    @JsonAlias("valor")
    private Long amount;
    @JsonAlias("tipo")
    private String type;
    @JsonAlias("realizada_em")
    private LocalDateTime date;



}