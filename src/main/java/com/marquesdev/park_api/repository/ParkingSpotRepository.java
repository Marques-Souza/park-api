package com.marquesdev.park_api.repository;

import com.marquesdev.park_api.entity.ParkingSpot;
import com.marquesdev.park_api.enums.StatusParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID> {

    Optional<ParkingSpot> findFirstByStatus(StatusParkingSpot statusParkingSpot);

    boolean existsByCode(String code);

    Optional<ParkingSpot> findByCode(String code);
}
