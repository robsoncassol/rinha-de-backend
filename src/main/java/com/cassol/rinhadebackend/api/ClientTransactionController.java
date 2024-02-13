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

@RestController
@RequestMapping("/clientes/{id}")
@AllArgsConstructor
public class ClientTransactionController {

    private final AccountTransactionService accountTransactionService;

    @PostMapping("/transacoes")
    public ResponseEntity<TransactionResult> create(@PathVariable("id") String id, @RequestBody @Validated TransactionRequest request) {
        return ResponseEntity.ok(accountTransactionService.transaction(parseIdToLong(id),
            Long.parseLong(request.getValor()),
            request.getTipo(),
            request.getDescricao()));
    }

    private Long parseIdToLong(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw new BusinessRuleException("Id é inválido");
        }
    }

    @GetMapping("/extrato")
    public ResponseEntity<Statement> getStatement(@PathVariable("id") String id) {
        return ResponseEntity.ok(accountTransactionService.statement(parseIdToLong(id)));
    }

}
