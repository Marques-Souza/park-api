package com.marquesdev.park_api;

import com.marquesdev.park_api.dto.UserLoginDto;
import com.marquesdev.park_api.jwt.JwtToken;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

public class JwtAuthentication {

    public static Consumer<HttpHeaders> getHeaderAuthentication(WebTestClient client, String username, String password){
        String token = client
                .post()
                .uri("/api/v1/auth")
                .bodyValue(new UserLoginDto(username, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody().getToken();
        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+token);
    }

}
