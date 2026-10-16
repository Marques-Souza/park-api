package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.VagaRequestDto;
import com.marquesdev.park_api.dto.VagaResponseDto;
import com.marquesdev.park_api.service.VagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.endpoints.vagas}")
@RequiredArgsConstructor
public class VagaController {

    private final VagaService vagaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDto> saveVaga(@Valid @RequestBody VagaRequestDto vagaRequestDto){
        VagaResponseDto vaga = vagaService.saveVaga(vagaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vaga);
    }

    @GetMapping("/{code}")
    public ResponseEntity<VagaResponseDto> findByCode(@Valid @PathVariable String code){
        VagaResponseDto vaga = vagaService.findByCode(code);
        return ResponseEntity.ok(vaga);
    }
}
