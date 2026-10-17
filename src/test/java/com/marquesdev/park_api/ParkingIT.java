package com.marquesdev.park_api;

import com.marquesdev.park_api.dto.ParkingRequestDto;
import com.marquesdev.park_api.dto.ParkingResponseDto;
import com.marquesdev.park_api.dto.ParkingSpotResponseDto;
import com.marquesdev.park_api.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parkings/parkings-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parkings/parkings-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class ParkingIT {

    @Autowired
    WebTestClient testClient;

    private static final String PARKINGS_URI = "/api/v1/parkings";
    private static final String ADMIN_USER = "parking-admin@gmail.com";
    private static final String CLIENT_USER = "parking-client@gmail.com";
    private static final String OTHER_CLIENT_USER = "other-parking-client@gmail.com";
    private static final String PASSWORD = "123456";
    private static final String CLIENT_CPF = "52998224725";
    private static final String ACTIVE_RECEIPT = "20261017-100001";
    private static final String ACTIVE_LICENSE_PLATE = "ACT-0001";

    @Test
    public void checkIn_WithValidData_ReturnsCreatedParkingSession() {
        ParkingResponseDto response = testClient.post()
                .uri(PARKINGS_URI + "/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .bodyValue(new ParkingRequestDto("Toyota", "Corolla", "Black", "NEW-1234", CLIENT_CPF))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ParkingResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getReceipt()).isNotBlank();
        assertThat(response.getEntryDate()).isNotNull();
        assertThat(response.getParkingCode()).isEqualTo("P002");
    }

    @Test
    public void checkIn_WithVehicleAlreadyParked_ReturnsStatus409() {
        ErrorMessage response = testClient.post()
                .uri(PARKINGS_URI + "/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .bodyValue(new ParkingRequestDto("Ford", "Focus", "Blue", ACTIVE_LICENSE_PLATE, CLIENT_CPF))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(409);
    }

    @Test
    public void checkIn_WithPreviouslyParkedLicensePlate_ReturnsCreatedParkingSession() {
        ParkingResponseDto response = testClient.post()
                .uri(PARKINGS_URI + "/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .bodyValue(new ParkingRequestDto("Toyota", "Corolla", "Black", "OLD-0001", CLIENT_CPF))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ParkingResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getLicensePlate()).isEqualTo("OLD-0001");
        assertThat(response.getReceipt()).isNotBlank();
    }

    @Test
    public void checkIn_WithInvalidData_ReturnsStatus422() {
        ErrorMessage response = testClient.post()
                .uri(PARKINGS_URI + "/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .bodyValue(new ParkingRequestDto("", "", "", "invalid", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    public void checkIn_WithoutAuthentication_ReturnsStatus401() {
        testClient.post()
                .uri(PARKINGS_URI + "/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ParkingRequestDto("Toyota", "Corolla", "Black", "NEW-1234", CLIENT_CPF))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void checkIn_WithUserRole_ReturnsStatus403() {
        testClient.post()
                .uri(PARKINGS_URI + "/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, CLIENT_USER, PASSWORD))
                .bodyValue(new ParkingRequestDto("Toyota", "Corolla", "Black", "NEW-1234", CLIENT_CPF))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void getByReceipt_WithActiveReceipt_ReturnsParkingSession() {
        ParkingResponseDto response = testClient.get()
                .uri(PARKINGS_URI + "/check-in/" + ACTIVE_RECEIPT)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, CLIENT_USER, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParkingResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getReceipt()).isEqualTo(ACTIVE_RECEIPT);
        assertThat(response.getLicensePlate()).isEqualTo(ACTIVE_LICENSE_PLATE);
    }

    @Test
    public void getByReceipt_WithUnknownReceipt_ReturnsStatus404() {
        ErrorMessage response = testClient.get()
                .uri(PARKINGS_URI + "/check-in/unknown-receipt")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void checkOut_WithActiveReceipt_CalculatesFeeAndReleasesSpot() {
        ParkingResponseDto response = testClient.put()
                .uri(PARKINGS_URI + "/check-out/" + ACTIVE_RECEIPT)
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParkingResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getExitDate()).isNotNull();
        assertThat(response.getPrice()).isNotNull().isGreaterThanOrEqualTo(new BigDecimal("5.00"));
        assertThat(response.getDiscount()).isNotNull();

        ParkingSpotResponseDto availableSpot = testClient.get()
                .uri("/api/v1/parking-spots/P001")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParkingSpotResponseDto.class)
                .returnResult().getResponseBody();
        assertThat(availableSpot).isNotNull();
        assertThat(availableSpot.getStatus()).isEqualTo("FREE");
    }

    @Test
    public void checkOut_WithUnknownReceipt_ReturnsStatus404() {
        ErrorMessage response = testClient.put()
                .uri(PARKINGS_URI + "/check-out/unknown-receipt")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void getParkingHistoryByCpf_WithAdmin_ReturnsPage() {
        PageWrapper<ParkingResponseDto> response = testClient.get()
                .uri(PARKINGS_URI + "/cpf/" + CLIENT_CPF + "?page=0&size=2")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageWrapper<ParkingResponseDto>>() {})
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void getMyParkingHistory_WithClient_ReturnsOnlyAuthenticatedClientsPage() {
        PageWrapper<ParkingResponseDto> response = testClient.get()
                .uri(PARKINGS_URI + "/me?page=0&size=5")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, CLIENT_USER, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageWrapper<ParkingResponseDto>>() {})
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getContent())
                .allSatisfy(parking -> assertThat(parking.getClientCpf()).isEqualTo(CLIENT_CPF));
    }

    @Test
    public void getMyParkingHistory_WithoutAuthentication_ReturnsStatus401() {
        testClient.get()
                .uri(PARKINGS_URI + "/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getMyParkingHistory_WithAdminRole_ReturnsStatus403() {
        testClient.get()
                .uri(PARKINGS_URI + "/me")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, ADMIN_USER, PASSWORD))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void getMyParkingHistory_DoesNotReturnOtherClientsSessions() {
        PageWrapper<ParkingResponseDto> response = testClient.get()
                .uri(PARKINGS_URI + "/me")
                .headers(JwtAuthentication.getHeaderAuthentication(testClient, OTHER_CLIENT_USER, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageWrapper<ParkingResponseDto>>() {})
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getClientCpf()).isEqualTo("83424546062");
    }
}
