package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.ParkingSpotRequestDto;
import com.marquesdev.park_api.dto.ParkingSpotResponseDto;
import com.marquesdev.park_api.entity.ParkingSpot;
import com.marquesdev.park_api.exception.EntityNotFoundException;
import com.marquesdev.park_api.exception.ParkingSpotViolationException;
import com.marquesdev.park_api.mapper.ParkingSpotMapper;
import com.marquesdev.park_api.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.marquesdev.park_api.enums.StatusParkingSpot.FREE;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSpotMapper parkingSpotMapper;

    @Transactional
    public ParkingSpotResponseDto saveParkingSpot(ParkingSpotRequestDto parkingSpotRequestDto){
        if (parkingSpotRepository.existsByCode(parkingSpotRequestDto.getCode())){
            throw new ParkingSpotViolationException("ParkingSpot already exists");
        }
        ParkingSpot parkingSpot = parkingSpotMapper.toParkingSpot(parkingSpotRequestDto);
        ParkingSpot savedParkingSpot = parkingSpotRepository.save(parkingSpot);
        return parkingSpotMapper.toDto(savedParkingSpot);
    }

    @Transactional(readOnly = true)
    public ParkingSpotResponseDto findByCode(String code){
        ParkingSpot parkingSpot = parkingSpotRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("ParkingSpot not found"));
        return parkingSpotMapper.toDto(parkingSpot);
    }

    @Transactional(readOnly = true)
    public ParkingSpot findParkingSpotFree() {
        return parkingSpotRepository.findFirstByStatus(FREE)
                .orElseThrow(() -> new EntityNotFoundException("No free parking spot available"));
    }
}
