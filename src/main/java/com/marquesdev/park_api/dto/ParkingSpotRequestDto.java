package com.marquesdev.park_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotRequestDto {

    @NotBlank(message = "Código da vaga cannot be null")
    @Size(min = 4, max = 4, message = "Código da vaga must be 4 characters")
    private String code;

    @NotBlank(message = "Status cannot be null")
    @Pattern(regexp = "(FREE|OCCUPIED)")
    private String status;
}
