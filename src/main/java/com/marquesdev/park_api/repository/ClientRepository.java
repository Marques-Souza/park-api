package com.marquesdev.park_api.repository;

import com.marquesdev.park_api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByCpf(String cpf);

    Optional<Client> findByUserId(UUID id);

    Optional<Client> findByCpf(String cpf);
}
