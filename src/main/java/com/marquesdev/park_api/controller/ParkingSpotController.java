package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.ParkingSpotRequestDto;
import com.marquesdev.park_api.dto.ParkingSpotResponseDto;
import com.marquesdev.park_api.service.ParkingSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.endpoints.parking-spots}")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpotResponseDto> saveParkingSpot(@Valid @RequestBody ParkingSpotRequestDto parkingSpotRequestDto){
        ParkingSpotResponseDto ParkingSpot = parkingSpotService.saveParkingSpot(parkingSpotRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ParkingSpot);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ParkingSpotResponseDto> findByCode(
            @PathVariable String code){
        ParkingSpotResponseDto ParkingSpot = parkingSpotService.findByCode(code);
        return ResponseEntity.ok(ParkingSpot);
    }
}
