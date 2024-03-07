package com.cassol.rinhadebackend.api;

import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionRequest;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.exceptions.BusinessRuleException;
import com.cassol.rinhadebackend.service.AccountTransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes/{id}")
@AllArgsConstructor
public class ClientTransactionController {

    private final AccountTransactionService accountTransactionService;

    @PostMapping("/transacoes")
    public Mono<TransactionResult> create(@PathVariable("id") String id, @RequestBody @Validated TransactionRequest request) {
        return Mono.just(accountTransactionService.transaction(parseIdToLong(id),
                Long.parseLong(request.getValor()),
                validateType(request),
                request.getDescricao()));
    }

    private String validateType(TransactionRequest request) {
        String tipo = request.getTipo();
        if ("C".equalsIgnoreCase(tipo) || "D".equalsIgnoreCase(tipo)) {
            return tipo;
        }
        throw new BusinessRuleException("Tipo é inválido");
    }

    private Long parseIdToLong(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw new BusinessRuleException("Id é inválido");
        }
    }

    @GetMapping("/extrato")
    public Mono<Statement> getStatement(@PathVariable("id") String id) {
        return Mono.just(accountTransactionService.statement(parseIdToLong(id)));
    }

}
