package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.ClientRequestDto;
import com.marquesdev.park_api.dto.ClientResponseDto;
import com.marquesdev.park_api.entity.Client;
import com.marquesdev.park_api.entity.User;
import com.marquesdev.park_api.exception.CpfViolationException;
import com.marquesdev.park_api.exception.EntityNotFoundException;
import com.marquesdev.park_api.mapper.ClientMapper;
import com.marquesdev.park_api.repository.ClientRepository;
import com.marquesdev.park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;

    @Transactional
    public ClientResponseDto saveClient(ClientRequestDto clientRequestDto, UUID userId) {
        if (clientRepository.existsByCpf(clientRequestDto.getCpf())) {
            throw new CpfViolationException("CPF already exists. ");
        }

        Client client = clientMapper.toClient(clientRequestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        client.setUser(user);

        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);


    }

    @Transactional(readOnly = true)
    public ClientResponseDto getById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponseDto> getAll(Pageable pageable) {
        Page<Client> clients = clientRepository.findAll(pageable);
        return clients.map(clientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ClientResponseDto getDetails(UUID id) {
        Client client = clientRepository.findByUserId(id);
        return clientMapper.toDto(client);

    }

    @Transactional(readOnly = true)
    public Client findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Client not found with cpf: " + cpf)));
    }
}
