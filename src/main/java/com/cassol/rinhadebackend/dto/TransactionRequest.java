package com.cassol.rinhadebackend.dto;

import com.cassol.rinhadebackend.model.TransactionOperation;

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

    @NotNull(message = "Valor é mandatório")
    @Min(1)
    private Long valor;
    @NotBlank(message = "Tipo é mandatório")
    @NotNull
    @Size(min = 1, max = 1)
    private TransactionOperation tipo;
    @NotBlank(message = "Descrição é mandatório")
    @NotNull
    @Size(min = 1, max = 10)
    private String descricao;
}
