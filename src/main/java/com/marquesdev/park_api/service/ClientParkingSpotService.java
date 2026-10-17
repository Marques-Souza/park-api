package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.ParkingResponseDto;
import com.marquesdev.park_api.entity.ClientParkingSpot;
import com.marquesdev.park_api.exception.EntityNotFoundException;
import com.marquesdev.park_api.mapper.ClientParkingSpotMapper;
import com.marquesdev.park_api.repository.ClientParkingSpotRepository;
import com.marquesdev.park_api.repository.projection.ClientParkingSpotProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientParkingSpotService {

    private final ClientParkingSpotRepository clientParkingSpotRepository;
    private final ClientParkingSpotMapper clientParkingSpotMapper;

    @Transactional
    public ParkingResponseDto saveClientParkingSpot(ClientParkingSpot clientParkingSpot){
        ClientParkingSpot savedClientParkingSpot = clientParkingSpotRepository.save(clientParkingSpot);
        return clientParkingSpotMapper.toDto(savedClientParkingSpot);
    }

    @Transactional(readOnly = true)
    public ClientParkingSpot findByReceipt(String receipt) {
        return clientParkingSpotRepository.findByReceiptAndExitDateIsNull(receipt)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No active parking session found for receipt: " + receipt));
    }

    @Transactional(readOnly = true)
    public long countTotalVezes(String cpf) {
        return clientParkingSpotRepository.countByClientCpfAndExitDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveParkingSession(String licensePlate) {
        return clientParkingSpotRepository.existsByLicensePlateAndExitDateIsNull(licensePlate);
    }

    @Transactional(readOnly = true)
    public Page<ParkingResponseDto> getAllByClientCpf(String cpf, Pageable pageable) {
        return clientParkingSpotRepository.findAllProjectionsByClientCpf(cpf, pageable)
                .map(clientParkingSpotMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ParkingResponseDto> getAllByClientUserId(UUID userId, Pageable pageable) {
        return clientParkingSpotRepository.findAllProjectionsByClientUserId(userId, pageable)
                .map(clientParkingSpotMapper::toDto);
    }
}
