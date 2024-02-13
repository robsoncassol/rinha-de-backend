package com.cassol.rinhadebackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\d{1,10}$")
    private String valor;
    @NotBlank(message = "Tipo é mandatório")
    @NotNull
    @Size(min = 1, max = 1)
    private String tipo;

    @Size.List({
        @Size(min = 1, message = "Descricao é muito pequena"),
        @Size(max = 10, message = "Descrião é muito longa")
    })
    private String descricao;
}
