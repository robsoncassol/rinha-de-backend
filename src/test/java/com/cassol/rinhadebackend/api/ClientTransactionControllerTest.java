package com.cassol.rinhadebackend.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cassol.rinhadebackend.dto.TransactionRequest;
import com.cassol.rinhadebackend.model.TransactionOperation;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class ClientTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateTransaction() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
            .descricao("Test deposit")
            .tipo(TransactionOperation.D)
            .valor(100L)
            .build();
        String requestBody = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/1/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.saldo").exists())
            .andExpect(jsonPath("$.limite").exists());
    }

    @Test
    public void testGetStatement() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
            .descricao("Test statement deposit")
            .tipo(TransactionOperation.D)
            .valor(100L)
            .build();
        String requestBody = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/2/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/2/extrato"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transactions").exists())
            .andExpect(jsonPath("$.transactions.length()").value(1));
    }
}