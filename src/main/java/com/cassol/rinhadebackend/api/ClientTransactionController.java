package com.cassol.rinhadebackend.api;

import com.cassol.rinhadebackend.dto.Statement;
import com.cassol.rinhadebackend.dto.TransactionRequest;
import com.cassol.rinhadebackend.dto.TransactionResult;
import com.cassol.rinhadebackend.service.AccountTransactionService;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TransactionResult> create(@PathVariable("id") Long id, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(accountTransactionService.transaction(id, request.getValor(), request.getTipo(), request.getDescricao()));
    }

    @GetMapping("/extrato")
    public ResponseEntity<Statement> getStatement(@PathVariable("id") Long id) {
        return ResponseEntity.ok(accountTransactionService.statement(id));
    }

}
