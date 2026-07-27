package com.myapp.taskmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    // Notamos que no hay campos internos de auditoria ni datos sensibles
    // Solo lo que el cliente necesita ver
}
