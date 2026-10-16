package com.marquesdev.park_api;

import com.marquesdev.park_api.dto.VagaRequestDto;
import com.marquesdev.park_api.dto.VagaResponseDto;
import com.marquesdev.park_api.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/vagas/vagas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/vagas/vagas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class VagaIT {

    @Autowired
    WebTestClient testClient;

    private static final String VAGAS_URI = "/api/v1/vagas";
    private static final UUID VAGA_ID =
            UUID.fromString("7ba7b810-9dad-11d1-80b4-00c04fd430ca");

    private final String ADMIN_USER = "supporte@gmail.com";
    private final String ADMIN_PASS = "123456";
    private final String ATTENDANT_USER = "sergio@gmail.com";
    private final String ATTENDANT_PASS = "123456";

    @Test
    public void createVaga_WithValidData_ReturnsCreatedVagaWithStatus201() {
        VagaResponseDto responseBody = testClient
                .post()
                .uri(VAGAS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .bodyValue(new VagaRequestDto("B002", "FREE"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VagaResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCode()).isEqualTo("B002");
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo("FREE");
        org.assertj.core.api.Assertions.assertThat(responseBody.getUrl()).contains(VAGAS_URI);
    }

    @Test
    public void createVaga_WithDuplicateCode_ReturnsStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(VAGAS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .bodyValue(new VagaRequestDto("A001", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createVaga_WithInvalidData_ReturnsStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(VAGAS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .bodyValue(new VagaRequestDto("", "INVALID"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createVaga_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .post()
                .uri(VAGAS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new VagaRequestDto("B002", "FREE"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void createVaga_WithUserRole_ReturnsStatus403() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(VAGAS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .bodyValue(new VagaRequestDto("B002", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void getVagaByCode_WithExistingCode_ReturnsVagaWithStatus200() {
        VagaResponseDto responseBody = testClient
                .get()
                .uri(VAGAS_URI + "/A001")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(VagaResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(VAGA_ID);
        org.assertj.core.api.Assertions.assertThat(responseBody.getCode()).isEqualTo("A001");
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo("FREE");
    }

    @Test
    public void getVagaByCode_WithInexistentCode_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri(VAGAS_URI + "/Z999")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getVagaByCode_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(VAGAS_URI + "/A001")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
