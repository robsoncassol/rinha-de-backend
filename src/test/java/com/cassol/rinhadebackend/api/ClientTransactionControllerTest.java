package com.cassol.rinhadebackend.api;

import com.cassol.rinhadebackend.dto.TransactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ClientTransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateTransaction() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .descricao("desc")
                .tipo("D")
                .valor("100")
                .build();
        webTestClient.post().uri("/clientes/1/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.saldo").exists()
                .jsonPath("$.limite").exists();
    }

    @Test
    public void testValidateTransactionInputType() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .descricao("Test")
                .tipo("X")
                .valor("100")
                .build();
        webTestClient.post().uri("/clientes/1/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void testValidateTransactionInputLongDescription() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .descricao("Test deposit gigantesca")
                .tipo("C")
                .valor("100")
                .build();
        webTestClient.post().uri("/clientes/1/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void testValidateTransactionInputShortDescription() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .descricao("")
                .tipo("C")
                .valor("100")
                .build();
        webTestClient.post().uri("/clientes/1/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void testValidateTransactionInputNullDescription() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .tipo("C")
                .valor("100")
                .build();
        webTestClient.post().uri("/clientes/1/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void testValidateTransactionInputDoubleAmount() throws Exception {
        String requestBody = "{\"valor\":1.2,\"tipo\":\"C\",\"descricao\":\"Descricao\"}";
        webTestClient.post().uri("/clientes/1/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void testGetStatement() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .descricao("Test")
                .tipo("D")
                .valor("100")
                .build();
        webTestClient.post().uri("/clientes/5/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk();
        webTestClient.get().uri("/clientes/5/extrato")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ultimas_transacoes").exists()
                .jsonPath("$.ultimas_transacoes.length()").isEqualTo(1);
    }
}