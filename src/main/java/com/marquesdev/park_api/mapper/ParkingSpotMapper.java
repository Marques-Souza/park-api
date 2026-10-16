package com.marquesdev.park_api.mapper;

import com.marquesdev.park_api.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.park_api.dto.ParkingSpotRequestDto;
import com.marquesdev.park_api.dto.ParkingSpotResponseDto;
import com.marquesdev.park_api.entity.ParkingSpot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ParkingSpotMapper {

    @Value("${app.api.endpoints.parking-spots}")
    private String parkingSpotEndpoint;

    private  final ModelMapper modelMapper;
    private final ResourceUrlBuilder resourceUrlBuilder;

    public ParkingSpot toParkingSpot(ParkingSpotRequestDto parkingSpotRequestDto){
        return modelMapper.map(parkingSpotRequestDto, ParkingSpot.class);
    }

    public ParkingSpotResponseDto toDto(ParkingSpot parkingSpot){
        ParkingSpotResponseDto response = modelMapper.map(parkingSpot, ParkingSpotResponseDto.class);

        response.setUrl(
                resourceUrlBuilder.build(parkingSpotEndpoint, parkingSpot.getId())
        );
        return response;
    }


}
