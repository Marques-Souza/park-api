package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.ParkingRequestDto;
import com.marquesdev.park_api.dto.ParkingResponseDto;
import com.marquesdev.park_api.exception.ErrorMessage;
import com.marquesdev.park_api.jwt.JwtUserDetails;
import com.marquesdev.park_api.service.ClientParkingSpotService;
import com.marquesdev.park_api.service.ClientService;
import com.marquesdev.park_api.service.ParkingReportService;
import com.marquesdev.park_api.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("${app.api.endpoints.parkings}")
@RequiredArgsConstructor
@Tag(name = "Parkings", description = "Operations for parking check-in, check-out, and parking history.")
public class ParkingController {

    private final ParkingService parkingService;
    private final ClientParkingSpotService clientParkingSpotService;
    private final ClientService clientService;
    private final ParkingReportService parkingReportService;

    @Operation(summary = "Check in a vehicle",
            description = "Starts a parking session using the first available parking spot. Requires authentication with the ADMIN role.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Parking session started successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to start a parking session",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Client or free parking spot not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Vehicle already has an active parking session",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid request fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkIn(@Valid @RequestBody ParkingRequestDto parkingRequestDto) {
        ParkingResponseDto parking = parkingService.checkIn(parkingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(parking);
    }

    @Operation(summary = "Retrieve an active parking session by receipt",
            description = "Returns the active parking session matching the receipt. Requires authentication with the ADMIN or USER role.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parking session retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to retrieve the parking session",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "No active parking session found for this receipt",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ParkingResponseDto> getByReceipt(
            @Parameter(description = "Receipt identifying the active parking session", example = "20261016-135754")
            @PathVariable String receipt) {
        ParkingResponseDto parking = parkingService.findByReceipt(receipt);
        return ResponseEntity.ok(parking);
    }

    @Operation(summary = "Check out a vehicle",
            description = "Ends the parking session, calculates the fee and discount, and releases the parking spot. Requires authentication with the ADMIN role.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parking session ended successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to end a parking session",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "No active parking session found for this receipt",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkout(
            @Parameter(description = "Receipt identifying the active parking session", example = "20261016-135754")
            @PathVariable String receipt) {
        ParkingResponseDto parking = parkingService.checkOut(receipt);
        return ResponseEntity.ok(parking);
    }

    @Operation(summary = "Retrieve a client's parking history",
            description = "Returns a paginated parking history for the supplied CPF. Requires authentication with the ADMIN role.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parking history retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to retrieve parking history",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ParkingResponseDto>> getAllParkingSessionsByClientCpf(
            @Parameter(description = "CPF of the client whose parking history is requested", example = "12042112054")
            @PathVariable String cpf,
            @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<ParkingResponseDto> parkings = clientParkingSpotService.getAllByClientCpf(cpf, pageable);
        return ResponseEntity.ok(parkings);
    }

    @Operation(summary = "Retrieve the authenticated client's parking history",
            description = "Returns a paginated parking history for the authenticated user. The user ID is taken from the JWT token. Requires authentication with the USER role.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parking history retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to retrieve parking history",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ParkingResponseDto>> getMyParkingSessions(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<ParkingResponseDto> parkings =
                clientParkingSpotService.getAllByClientUserId(userDetails.getId(), pageable);
        return ResponseEntity.ok(parkings);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> getReport(HttpServletResponse response, @AuthenticationPrincipal JwtUserDetails user) throws IOException {
        String cpf = clientService.findByUserId(user.getId()).getCpf();
        byte[] bytes = parkingReportService.generateReport(cpf);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);

        return ResponseEntity.ok().build();

    }
}
