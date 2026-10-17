package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.ParkingRequestDto;
import com.marquesdev.park_api.dto.ParkingResponseDto;
import com.marquesdev.park_api.jwt.JwtUserDetails;
import com.marquesdev.park_api.service.ClientParkingSpotService;
import com.marquesdev.park_api.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${app.api.endpoints.parkings}")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;
    private final ClientParkingSpotService clientParkingSpotService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkIn(@Valid @RequestBody ParkingRequestDto parkingRequestDto) {
        ParkingResponseDto parking = parkingService.checkIn(parkingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(parking);
    }

    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<ParkingResponseDto> getByReceipt(@PathVariable String receipt) {
        ParkingResponseDto parking = parkingService.findByReceipt(receipt);
        return ResponseEntity.ok(parking);
    }

    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkout(@PathVariable String receipt) {
        ParkingResponseDto parking = parkingService.checkOut(receipt);
        return ResponseEntity.ok(parking);
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ParkingResponseDto>> getAllParkingSessionsByClientCpf(
            @PathVariable String cpf,
            @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<ParkingResponseDto> parkings = clientParkingSpotService.getAllByClientCpf(cpf, pageable);
        return ResponseEntity.ok(parkings);
    }

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
}
