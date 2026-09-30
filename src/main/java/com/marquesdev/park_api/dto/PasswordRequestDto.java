package com.marquesdev.park_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PasswordRequestDto {

    @NotBlank(message = "Current password cannot be blank.")
    @Size(min = 6, max = 6, message = "Current password must be exactly 6 characters.")
    private String currentPassword;

    @NotBlank(message = "New password cannot be blank.")
    @Size(min = 6, max = 6, message = "New password must be exactly 6 characters.")
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be blank.")
    @Size(min = 6, max = 6, message = "Confirm password must be exactly 6 characters.")
    private String confirmPassword;
}
