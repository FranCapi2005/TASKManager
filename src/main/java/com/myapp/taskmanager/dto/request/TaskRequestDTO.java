package com.myapp.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequestDTO {

    @NotBlank(message = "El titulo no puede estar vacio") // validacion automatica
    @Size(min = 3, max = 100, message = "Entre 3 y 100 caracteres")
    private String title;

    private String description; // opcional, sin validacion

    private boolean completed;

    @NotNull(message = "El ID del usuario es requerido")
    private Long userId; // Referencia al usuario dueño de la tarea
}