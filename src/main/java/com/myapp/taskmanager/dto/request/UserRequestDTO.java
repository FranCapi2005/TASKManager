package com.myapp.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacio")
    private String name;

    @Email(message = "Formato de email no valido")
    @NotBlank(message = "El email no puede estar vacio")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacia")
    @Size(min = 6, message = "El tamaño de la contraseña debe ser minimo 6 caracteres")
    private String password;
}
