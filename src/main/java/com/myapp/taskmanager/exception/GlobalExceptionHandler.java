package com.myapp.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Intercepta excepciones de TODOS los controllers
// Equivalente a un middleware de error global en Express: app.use((err, req, res, next) => ...)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura TaskNotFoundException -> 404
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex){
        ErrorResponse error = new ErrorResponse(
                404,
                ex.getMessage(),    // "Task not found with ID: 123"
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Captura TaskAlreadyExistsException -> 409 Conflict
    @ExceptionHandler(TaskAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTaskAlreadyExists(TaskAlreadyExistsException ex){
        ErrorResponse error = new ErrorResponse(
                409,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Maneja errores de validacion (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handlerValidactionErrors(
            MethodArgumentNotValidException ex
    ){
        Map<String, String> fieldErrors = new HashMap<>();

        // Recorre cada campo que fallo validacion
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    // Maneja cualquier excepcion no controlada (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){
        ErrorResponse error = new ErrorResponse(
                500, "Internal server error", LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}