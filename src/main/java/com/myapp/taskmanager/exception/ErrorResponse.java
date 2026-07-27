// Esta es la estructura JSON que va a recibir el cliente cuando hay un error
package com.myapp.taskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
