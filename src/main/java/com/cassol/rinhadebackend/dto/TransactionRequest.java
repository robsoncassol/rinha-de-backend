package com.cassol.rinhadebackend.dto;

import com.cassol.rinhadebackend.model.TransactionOperation;
import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TransactionRequest {
    @JsonAlias("valor")
    @NotNull
    @Min(1)
    private Long amount;
    @JsonAlias("tipo")
    @NotBlank
    @NotNull
    @Size(min = 1, max = 1)
    private TransactionOperation type;
    @JsonAlias("descricao")
    @NotBlank
    @NotNull
    @Size(min = 1, max = 10)
    private String description;
}
