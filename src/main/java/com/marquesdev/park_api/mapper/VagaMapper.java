package com.marquesdev.park_api.mapper;

import com.marquesdev.park_api.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.park_api.dto.VagaRequestDto;
import com.marquesdev.park_api.dto.VagaResponseDto;
import com.marquesdev.park_api.entity.Vaga;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class VagaMapper {

    @Value("${app.api.endpoints.vagas}")
    private String vagaEndpoint;

    private  final ModelMapper modelMapper;
    private final ResourceUrlBuilder resourceUrlBuilder;

    public Vaga toVaga(VagaRequestDto vagaRequestDto){
        return modelMapper.map(vagaRequestDto, Vaga.class);
    }

    public VagaResponseDto toDto(Vaga vaga){
        VagaResponseDto response = modelMapper.map(vaga, VagaResponseDto.class);

        response.setUrl(
                resourceUrlBuilder.build(vagaEndpoint, vaga.getId())
        );
        return response;
    }


}
