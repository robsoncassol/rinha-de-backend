package com.cassol.rinhadebackend.dto;

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

    private Long limite;
    private Long total;
    private LocalDateTime data_extrato;

}
