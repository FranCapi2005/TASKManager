package com.myapp.taskmanager.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email(message = "The email format is not valid")
    @NotBlank(message = "The email input couldnt be empty")
    private String email;

    @NotBlank(message = "The password input couldnt be empty")
    private String password;
}
