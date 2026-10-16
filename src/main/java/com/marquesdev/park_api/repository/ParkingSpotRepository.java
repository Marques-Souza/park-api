package com.marquesdev.park_api.repository;

import com.marquesdev.park_api.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID> {

    boolean existsByCode(String code);

    Optional<ParkingSpot> findByCode(String code);
}
