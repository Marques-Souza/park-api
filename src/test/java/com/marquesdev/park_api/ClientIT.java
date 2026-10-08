package com.marquesdev.park_api;


import com.marquesdev.park_api.dto.ClientRequestDto;
import com.marquesdev.park_api.dto.ClientResponseDto;
import com.marquesdev.park_api.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clients/clients-insert.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clients/clients-delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class ClientIT {

    @Autowired
    WebTestClient testClient;

    // Constante para a URI base de clientes
    private static final String CLIENTS_URI = "/api/v1/clients";

    // Database IDs constants (SQL Script)
    private static final UUID ADMIN_ID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private static final UUID SERGIO_USER_ID =
            UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    private static final UUID KARLA_USER_ID =
            UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");

    private static final UUID KARLA_CLIENT_ID =
            UUID.fromString("7ba7b810-9dad-11d1-80b4-00c04fd430c9");

    private static final UUID INEXISTENT_ID =
            UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

    // Test credentials constants
    private final String ADMIN_USER = "supporte@gmail.com";
    private final String ADMIN_PASS = "123456";
    private final String SERGIO_USER = "sergio@gmail.com";
    private final String SERGIO_PASS = "123456";
    private final String KARLA_USER = "karla@gmail.com";
    private final String KARLA_PASS = "123456";

    // ===========================================
    // TEST BLOCK: POST /api/v1/clients (Create Client)
    // =============================================

    @Test
    public void createClient_WithValidData_ReturnsCreatedClientWithStatus201(){
        ClientResponseDto responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, SERGIO_USER, SERGIO_PASS))
                .bodyValue(new ClientRequestDto("João Silva", "52998224725"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("João Silva");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("52998224725");
    }

    @Test
    public void createClient_WithDuplicateCpf_ReturnsStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, SERGIO_USER, SERGIO_PASS))
                .bodyValue(new ClientRequestDto("Maria Santos", "83424546062"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createClient_WithInvalidData_ReturnsStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, SERGIO_USER, SERGIO_PASS))
                .bodyValue(new ClientRequestDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createClient_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .post()
                .uri(CLIENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("João Silva", "52998224725"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void createClient_WithAdminRole_ReturnsStatus403(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .bodyValue(new ClientRequestDto("João Silva", "52998224725"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/clients/{id} (Get Client by ID)
    // =============================================

    @Test
    public void getClientById_WithExistingId_ReturnsClientWithStatus200(){
        ClientResponseDto responseBody = testClient
                .get()
                .uri(CLIENTS_URI + "/" + KARLA_CLIENT_ID)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(KARLA_CLIENT_ID);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Karla santos da silva");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("83424546062");
    }

    @Test
    public void getClientById_WithInexistentId_ReturnsStatus404(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(CLIENTS_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/clients (Get All Clients)
    // =============================================

    @Test
    public void getAllClients_ReturnsListOfClientsWithStatus200(){
        PageWrapper<ClientResponseDto> responseBody = testClient
                .get()
                .uri(CLIENTS_URI)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageWrapper<ClientResponseDto>>() {})
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(1);
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/clients/details (Get Client Details)
    // =============================================

    @Test
    public void getClientDetails_WithValidUser_ReturnsClientWithStatus200(){
        ClientResponseDto responseBody = testClient
                .get()
                .uri(CLIENTS_URI + "/details")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, KARLA_USER, KARLA_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(KARLA_CLIENT_ID);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Karla santos da silva");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("83424546062");
    }

    @Test
    public void getClientDetails_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .get()
                .uri(CLIENTS_URI + "/details")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getClientDetails_WithAdminRole_ReturnsStatus403(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(CLIENTS_URI + "/details")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

}
