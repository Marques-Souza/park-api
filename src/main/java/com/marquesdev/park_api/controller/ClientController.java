package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.ClientRequestDto;
import com.marquesdev.park_api.dto.ClientResponseDto;
import com.marquesdev.park_api.entity.Client;
import com.marquesdev.park_api.exception.ErrorMessage;
import com.marquesdev.park_api.jwt.JwtUserDetails;
import com.marquesdev.park_api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Clients", description = "Contains all operations related to client resources for creating.")
@RestController
@RequestMapping("${app.api.endpoints.clients}")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Create a new client",
            description = "Create a new client in the database linked to the authenticated user (Bearer Token required).",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Client created successfully and linked to user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Client already exists for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody @Valid ClientRequestDto clientRequestDto,
                                                          @AuthenticationPrincipal JwtUserDetails userDetails){
        ClientResponseDto client = clientService.saveClient(clientRequestDto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getById(@PathVariable UUID id){
        ClientResponseDto client = clientService.getById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponseDto>> getAll(Pageable pageable){
        Pageable fixedPageable = PageRequest.of(
                pageable.getPageNumber(),
                10);
        Page<ClientResponseDto> clients = clientService.getAll(fixedPageable);
        return ResponseEntity.ok(clients);
    }


    @GetMapping("/details")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClientResponseDto> getDetails(@AuthenticationPrincipal JwtUserDetails userDetails){
       ClientResponseDto client = clientService.getDetails(userDetails.getId());
        return ResponseEntity.ok(client);
    }
}
