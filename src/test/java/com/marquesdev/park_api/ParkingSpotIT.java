package com.marquesdev.park_api;

import com.marquesdev.park_api.dto.ParkingSpotRequestDto;
import com.marquesdev.park_api.dto.ParkingSpotResponseDto;
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
@Sql(scripts = "/sql/parking-spots/parking-spots-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking-spots/parking-spots-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class ParkingSpotIT {

    @Autowired
    WebTestClient testClient;

    private static final String PARKING_SPOTS_URI = "/api/v1/parking-spots";
    private static final UUID PARKING_SPOT_ID = UUID.fromString("7ba7b810-9dad-11d1-80b4-00c04fd430ca");

    private final String ADMIN_USER = "supporte@gmail.com";
    private final String ADMIN_PASS = "123456";
    private final String USER_USERNAME = "sergio@gmail.com";
    private final String USER_PASSWORD = "123456";

    @Test
    public void createParkingSpot_WithValidData_ReturnsCreatedParkingSpotWithStatus201() {
        ParkingSpotResponseDto responseBody = testClient.post().uri(PARKING_SPOTS_URI).contentType(MediaType.APPLICATION_JSON).headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS)).bodyValue(new ParkingSpotRequestDto("B002", "FREE")).exchange().expectStatus().isCreated().expectBody(ParkingSpotResponseDto.class).returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCode()).isEqualTo("B002");
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo("FREE");
        org.assertj.core.api.Assertions.assertThat(responseBody.getUrl()).contains(PARKING_SPOTS_URI);
    }

    @Test
    public void createParkingSpot_WithDuplicateCode_ReturnsStatus409() {
        ErrorMessage responseBody = testClient.post().uri(PARKING_SPOTS_URI).contentType(MediaType.APPLICATION_JSON).headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS)).bodyValue(new ParkingSpotRequestDto("A001", "FREE")).exchange().expectStatus().isEqualTo(409).expectBody(ErrorMessage.class).returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createParkingSpot_WithInvalidData_ReturnsStatus422() {
        ErrorMessage responseBody = testClient.post().uri(PARKING_SPOTS_URI).contentType(MediaType.APPLICATION_JSON).headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS)).bodyValue(new ParkingSpotRequestDto("", "INVALID")).exchange().expectStatus().isEqualTo(422).expectBody(ErrorMessage.class).returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createParkingSpot_WithoutAuthentication_ReturnsStatus401() {
        testClient.post().uri(PARKING_SPOTS_URI).contentType(MediaType.APPLICATION_JSON).bodyValue(new ParkingSpotRequestDto("B002", "FREE")).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void createParkingSpot_WithUserRole_ReturnsStatus403() {
        ErrorMessage responseBody = testClient.post().uri(PARKING_SPOTS_URI).contentType(MediaType.APPLICATION_JSON).headers(JwtAuthentication.getHeaderAuthentication(testClient, USER_USERNAME, USER_PASSWORD)).bodyValue(new ParkingSpotRequestDto("B002", "FREE")).exchange().expectStatus().isEqualTo(403).expectBody(ErrorMessage.class).returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void getParkingSpotByCode_WithExistingCode_ReturnsParkingSpotWithStatus200() {
        ParkingSpotResponseDto responseBody = testClient.get().uri(PARKING_SPOTS_URI + "/A001").headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS)).exchange().expectStatus().isOk().expectBody(ParkingSpotResponseDto.class).returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(PARKING_SPOT_ID);
        org.assertj.core.api.Assertions.assertThat(responseBody.getCode()).isEqualTo("A001");
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo("FREE");
    }

    @Test
    public void getParkingSpotByCode_WithInexistentCode_ReturnsStatus404() {
        ErrorMessage responseBody = testClient.get().uri(PARKING_SPOTS_URI + "/Z999").headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, ADMIN_PASS)).exchange().expectStatus().isEqualTo(404).expectBody(ErrorMessage.class).returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getParkingSpotByCode_WithoutAuthentication_ReturnsStatus401() {
        testClient.get().uri(PARKING_SPOTS_URI + "/A001").exchange().expectStatus().isUnauthorized();
    }
}
