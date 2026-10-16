package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.VagaRequestDto;
import com.marquesdev.park_api.dto.VagaResponseDto;
import com.marquesdev.park_api.entity.Vaga;
import com.marquesdev.park_api.exception.EntityNotFoundException;
import com.marquesdev.park_api.exception.VagaViolationException;
import com.marquesdev.park_api.mapper.VagaMapper;
import com.marquesdev.park_api.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;
    private final VagaMapper vagaMapper;

    @Transactional
    public VagaResponseDto saveVaga(VagaRequestDto vagaRequestDto){
        if (vagaRepository.existsByCode(vagaRequestDto.getCode())){
            throw new VagaViolationException("Vaga already exists");
        }
        Vaga vaga = vagaMapper.toVaga(vagaRequestDto);
        Vaga savedVaga = vagaRepository.save(vaga);
        return vagaMapper.toDto(savedVaga);
    }

    @Transactional(readOnly = true)
    public VagaResponseDto findByCode(String code){
        Vaga vaga = vagaRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Vaga not found"));
        return vagaMapper.toDto(vaga);
    }
}
