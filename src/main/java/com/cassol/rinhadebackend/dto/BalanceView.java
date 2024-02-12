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
public class BalanceView {

    @JsonAlias("limite")
    private Long limit;
    @JsonAlias("total")
    private Long balance;
    @JsonAlias("data_extrato")
    private LocalDateTime date;

}
