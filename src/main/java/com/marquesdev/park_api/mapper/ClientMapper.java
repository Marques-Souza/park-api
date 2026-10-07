package com.marquesdev.park_api.mapper;

import com.marquesdev.park_api.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.park_api.dto.ClientRequestDto;
import com.marquesdev.park_api.dto.ClientResponseDto;
import com.marquesdev.park_api.entity.Client;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ClientMapper {


    @Value("${app.api.endpoints.clients}")
    private String clientEndpoint;

    private final ModelMapper modelMapper;
    private final ResourceUrlBuilder resourceUrlBuilder;

    public Client toClient(ClientRequestDto clientRequestDto){
        return modelMapper.map(clientRequestDto, Client.class);}

    public ClientResponseDto toDto(Client client){
        ClientResponseDto response = modelMapper.map(client, ClientResponseDto.class);

        response.setUrl(
                resourceUrlBuilder.build(clientEndpoint, client.getId())
        );
        return response;
    }
}
