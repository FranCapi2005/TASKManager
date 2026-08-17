package com.myapp.taskmanager.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @NotBlank(message = "The refresh token couldnt be empty")
    private String refreshToken;
}
