package com.myapp.taskmanager.dto.request;

import com.myapp.taskmanager.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "The name couldnt be empty")
    private String name;

    @Email(message = "Email format invalid")
    @NotBlank(message = "The email couldnt be empty")
    private String email;

    @NotBlank(message = "The password couldnt be empty")
    @Size(min = 6, message = "The password length should be 6 or more")
    private String password;

    private Role role;
}
