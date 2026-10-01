package com.marquesdev.park_api;


import com.marquesdev.park_api.dto.PasswordRequestDto;
import com.marquesdev.park_api.dto.UserRequestDto;
import com.marquesdev.park_api.dto.UserResponseDto;
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
@Sql(scripts = "/sql/users/users-insert.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class UserIT {

    @Autowired
    WebTestClient testClient;

    // Constante para a URI base de usuários
    private static final String USERS_URI = "/api/v1/users";

    // Database IDs constants (SQL Script)
    private static final UUID ADMIN_ID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private static final UUID USER_ID =
            UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    private static final UUID INEXISTENT_ID =
            UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

    // Test credentials constants
    private final String ADMIN_USER = "supporte@gmail.com";
    private final String ADMIN_PASS = "123456";
    private final String ATTENDANT_USER = "sergio@gmail.com";
    private final String ATTENDANT_PASS = "123456";


    // ===========================================
    // TEST BLOCK: POST /api/v1/users (Create User)
    // =============================================

    @Test
    public void createUser_WithValidCredentials_ReturnsCreatedUserWithStatus201(){
        UserResponseDto responseBody = testClient
                .post()
                .uri(USERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserRequestDto("cleber@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("cleber@gmail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("USER");
    }

    @Test
    public void createUser_WithDuplicateUsername_ReturnsStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(USERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserRequestDto(ADMIN_USER, "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createUser_WithInvalidData_ReturnsStatus422(){
        ErrorMessage reponseBody = testClient
                .post()
                .uri(USERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserRequestDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(reponseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(reponseBody.getStatus()).isEqualTo(422);
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/users/{id} (Get User by ID)
    // =============================================

    @Test
    public void getUserById_WithExistingId_ReturnsUserWithStatus200(){
        UserResponseDto responseBody = testClient
                .get()
                .uri(USERS_URI + "/" + ADMIN_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(ADMIN_ID);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo(ADMIN_USER);
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void getUserById_WithInexistentId_ReturnsStatus404(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(USERS_URI + "/" + INEXISTENT_ID)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/users (Get All Users)
    // =============================================

    @Test
    public void getAllUsers_ReturnsListOfUsersWithStatus200(){
        UserResponseDto[] responseBody = testClient
                .get()
                .uri(USERS_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto[].class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.length).isEqualTo(3);
    }

    // ===========================================
    // TEST BLOCK: PATCH /api/v1/users/{id}/password (Update Password)
    // =============================================

    @Test
    public void updatePassword_WithValidData_ReturnsStatus200(){
        UserResponseDto responseBody = testClient
                .patch()
                .uri(USERS_URI + "/" + USER_ID + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PasswordRequestDto("123456", "654321", "654321"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
    }

    @Test
    public void updatePassword_WithInvalidCurrentPassword_ReturnsStatus400(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(USERS_URI + "/" + USER_ID + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PasswordRequestDto("000000", "654321", "654321"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updatePassword_WithMismatchedPasswords_ReturnsStatus400(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(USERS_URI + "/" + USER_ID + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PasswordRequestDto("123456", "654321", "111111"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updatePassword_WithInvalidData_ReturnsStatus422(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(USERS_URI + "/" + USER_ID + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PasswordRequestDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updatePassword_WithInexistentId_ReturnsStatus404(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(USERS_URI + "/" + INEXISTENT_ID + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PasswordRequestDto("123456", "654321", "654321"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

}

