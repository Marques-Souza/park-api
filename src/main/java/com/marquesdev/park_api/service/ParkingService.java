package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.ParkingRequestDto;
import com.marquesdev.park_api.dto.ParkingResponseDto;
import com.marquesdev.park_api.entity.Client;
import com.marquesdev.park_api.entity.ClientParkingSpot;
import com.marquesdev.park_api.entity.ParkingSpot;
import com.marquesdev.park_api.exception.ParkingSpotViolationException;
import com.marquesdev.park_api.enums.StatusParkingSpot;
import com.marquesdev.park_api.mapper.ClientParkingSpotMapper;
import com.marquesdev.park_api.utils.ParkingDiscountUtils;
import com.marquesdev.park_api.utils.ParkingFeeUtils;
import com.marquesdev.park_api.utils.ParkingSpotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ClientParkingSpotService clientParkingSpotService;
    private final ParkingSpotService parkingSpotService;
    private final ClientService clientService;
    private final ClientParkingSpotMapper clientParkingSpotMapper;

    @Transactional
    public ParkingResponseDto checkIn(ParkingRequestDto request) {
        Client client = clientService.findByCpf(request.getClientCpf());
        if (clientParkingSpotService.hasActiveParkingSession(request.getLicensePlate())) {
            throw new ParkingSpotViolationException(
                    "This vehicle already has an active parking session");
        }

        ClientParkingSpot clientParkingSpot = clientParkingSpotMapper.toClientParkingSpot(request);
        clientParkingSpot.setClient(client);
        ParkingSpot parkingSpot = parkingSpotService.findParkingSpotFree();
        parkingSpot.setStatus(StatusParkingSpot.OCCUPIED);
        clientParkingSpot.setParkingSpot(parkingSpot);

        clientParkingSpot.setEntryDate(LocalDateTime.now());
        clientParkingSpot.setReceipt(ParkingSpotUtils.generateReceipt());

        return clientParkingSpotService.saveClientParkingSpot(clientParkingSpot);
    }

    @Transactional(readOnly = true)
    public ParkingResponseDto findByReceipt(String receipt) {
        ClientParkingSpot clientParkingSpot = clientParkingSpotService.findByReceipt(receipt);
        return clientParkingSpotMapper.toDto(clientParkingSpot);
    }

    @Transactional
    public ParkingResponseDto checkOut(String receipt) {
        ClientParkingSpot clientParkingSpot = clientParkingSpotService.findByReceipt(receipt);

        LocalDateTime exitDate = LocalDateTime.now();
        BigDecimal price = ParkingFeeUtils.calculateFee(clientParkingSpot.getEntryDate(), exitDate);
        clientParkingSpot.setPrice(price);

        long countCompletedParkingSessions = clientParkingSpotService.countTotalVezes(clientParkingSpot.getClient().getCpf());

        BigDecimal discount = ParkingDiscountUtils.calculateDiscount(price, countCompletedParkingSessions + 1);
        clientParkingSpot.setDiscount(discount);
        clientParkingSpot.setExitDate(exitDate);
        clientParkingSpot.getParkingSpot().setStatus(StatusParkingSpot.FREE);
        return clientParkingSpotService.saveClientParkingSpot(clientParkingSpot);
    }


}
