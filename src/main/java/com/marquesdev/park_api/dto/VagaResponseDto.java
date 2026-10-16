package com.marquesdev.park_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VagaResponseDto {

    private UUID id;

    private String code;

    private String status;

    private String url;
}
