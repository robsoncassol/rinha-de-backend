package com.cassol.rinhadebackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewTransactionEvent {
    private Long amount;
    private String type;
    private String description;
    private Long accountId;
}
