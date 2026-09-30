package com.marquesdev.park_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRequestDto {

    @NotBlank(message = "Email cannot be blank.")
    @Email(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format."
    )
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters.")
    private String password;
}
