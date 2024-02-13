package com.cassol.rinhadebackend.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cassol.rinhadebackend.dto.TransactionRequest;
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
            .descricao("desc")
            .tipo("D")
            .valor("100")
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
    public void testValidateTransactionInputType() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
            .descricao("Test")
            .tipo("X")
            .valor("100")
            .build();
        String requestBody = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/1/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(422));
    }

    @Test
    public void testValidateTransactionInputLongDescription() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
            .descricao("Test deposit gigantesca")
            .tipo("C")
            .valor("100")
            .build();
        String requestBody = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/1/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(422));
    }

    @Test
    public void testValidateTransactionInputShortDescription() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
            .descricao("")
            .tipo("C")
            .valor("100")
            .build();
        String requestBody = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/1/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(422));
    }

    @Test
    public void testValidateTransactionInputDoubleAmount() throws Exception {
        String requestBody = "{\"valor\":100.99,\"tipo\":\"C\",\"descricao\":\"Descricao\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/1/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(422));
    }

    @Test
    public void testGetStatement() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
            .descricao("Test")
            .tipo("D")
            .valor("100")
            .build();
        String requestBody = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/5/transacoes")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/5/extrato"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ultimas_transacoes").exists())
            .andExpect(jsonPath("$.ultimas_transacoes.length()").value(1));
    }
}