package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.ParkingSpotRequestDto;
import com.marquesdev.park_api.dto.ParkingSpotResponseDto;
import com.marquesdev.park_api.exception.ErrorMessage;
import com.marquesdev.park_api.service.ParkingSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.endpoints.parking-spots}")
@RequiredArgsConstructor
@Tag(name = "Parking Spots", description = "Operations for creating and retrieving parking spots.")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @Operation(summary = "Create a parking spot",
            description = "Creates a parking spot. Requires authentication with the ADMIN role.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Parking spot created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingSpotResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to create a parking spot",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "A parking spot with this code already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpotResponseDto> saveParkingSpot(@Valid @RequestBody ParkingSpotRequestDto parkingSpotRequestDto){
        ParkingSpotResponseDto parkingSpot = parkingSpotService.saveParkingSpot(parkingSpotRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpot);
    }

    @Operation(summary = "Retrieve a parking spot by code",
            description = "Returns a parking spot matching the provided code. Requires authentication.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parking spot retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingSpotResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Parking spot not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{code}")
    public ResponseEntity<ParkingSpotResponseDto> findByCode(
            @Parameter(description = "Four-character parking spot code", example = "A001")
            @PathVariable String code){
        ParkingSpotResponseDto parkingSpot = parkingSpotService.findByCode(code);
        return ResponseEntity.ok(parkingSpot);
    }
}
