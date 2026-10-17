package com.marquesdev.park_api.repository;

import com.marquesdev.park_api.entity.ClientParkingSpot;
import com.marquesdev.park_api.repository.projection.ClientParkingSpotProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientParkingSpotRepository extends JpaRepository<ClientParkingSpot, UUID> {

    Optional<ClientParkingSpot> findByReceiptAndExitDateIsNull(String receipt);

    boolean existsByLicensePlateAndExitDateIsNull(String licensePlate);

    long countByClientCpfAndExitDateIsNotNull(String cpf);

    @Query(value = """
            select cps.id as id,
                   cps.brand as brand,
                   cps.model as model,
                   cps.color as color,
                   cps.licensePlate as licensePlate,
                   client.cpf as clientCpf,
                   cps.receipt as receipt,
                   cps.entryDate as entryDate,
                   cps.exitDate as exitDate,
                   parkingSpot.code as parkingCode,
                   cps.price as price,
                   cps.discount as discount
            from ClientParkingSpot cps
            join cps.client client
            join cps.parkingSpot parkingSpot
            where client.cpf = :cpf
            """,
            countQuery = """
            select count(cps)
            from ClientParkingSpot cps
            join cps.client client
            where client.cpf = :cpf
            """)
    Page<ClientParkingSpotProjection> findAllProjectionsByClientCpf(
            @Param("cpf") String cpf,
            Pageable pageable);

    @Query(value = """
            select cps.id as id,
                   cps.brand as brand,
                   cps.model as model,
                   cps.color as color,
                   cps.licensePlate as licensePlate,
                   client.cpf as clientCpf,
                   cps.receipt as receipt,
                   cps.entryDate as entryDate,
                   cps.exitDate as exitDate,
                   parkingSpot.code as parkingCode,
                   cps.price as price,
                   cps.discount as discount
            from ClientParkingSpot cps
            join cps.client client
            join cps.parkingSpot parkingSpot
            where client.user.id = :userId
            """,
            countQuery = """
            select count(cps)
            from ClientParkingSpot cps
            join cps.client client
            where client.user.id = :userId
            """)
    Page<ClientParkingSpotProjection> findAllProjectionsByClientUserId(
            @Param("userId") UUID userId,
            Pageable pageable);
}
