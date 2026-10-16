package com.marquesdev.park_api.repository;

import com.marquesdev.park_api.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VagaRepository extends JpaRepository<Vaga, UUID> {

    boolean existsByCode(String code);

    Optional<Vaga> findByCode(String code);
}
