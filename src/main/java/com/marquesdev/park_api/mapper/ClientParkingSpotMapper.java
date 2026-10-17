package com.marquesdev.park_api.mapper;

import com.marquesdev.park_api.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.park_api.dto.ParkingRequestDto;
import com.marquesdev.park_api.dto.ParkingResponseDto;
import com.marquesdev.park_api.entity.ClientParkingSpot;
import com.marquesdev.park_api.repository.projection.ClientParkingSpotProjection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ClientParkingSpotMapper {

    @Value("${app.api.endpoints.client-parking-spots}")
    private String clientParkingSpotEndpoint;

    private final ResourceUrlBuilder resourceUrlBuilder;
    private final ModelMapper modelMapper;

    public ClientParkingSpot toClientParkingSpot(ParkingRequestDto parkingRequestDto){
        return modelMapper.map(parkingRequestDto, ClientParkingSpot.class);
    }

    public ParkingResponseDto toDto(ClientParkingSpot clientParkingSpot ){
       ParkingResponseDto response = modelMapper.map(clientParkingSpot, ParkingResponseDto.class);

       response.setUrl(
               resourceUrlBuilder.build(clientParkingSpotEndpoint, clientParkingSpot.getId())
       );
       return response;
    }

    public ParkingResponseDto toDto(ClientParkingSpotProjection projection) {
        ParkingResponseDto response = modelMapper.map(projection, ParkingResponseDto.class);
        response.setUrl(resourceUrlBuilder.build(clientParkingSpotEndpoint, projection.getId()));
        return response;
    }
}
