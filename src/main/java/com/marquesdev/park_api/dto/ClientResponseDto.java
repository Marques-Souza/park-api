package com.marquesdev.park_api.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientResponseDto {

    private UUID id;
    private String name;
    private String cpf;
    private String url;
}
