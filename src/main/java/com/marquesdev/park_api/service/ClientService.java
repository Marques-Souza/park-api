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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
