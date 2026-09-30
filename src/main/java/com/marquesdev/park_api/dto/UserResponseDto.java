package com.marquesdev.park_api.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDto {

    private UUID id;
    private String username;
    private String role;
    private String url;
}
