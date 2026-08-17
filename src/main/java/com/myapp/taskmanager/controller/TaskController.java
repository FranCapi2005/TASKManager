package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.dto.response.TaskResponseDTO;
import com.myapp.taskmanager.dto.request.TaskRequestDTO;
import com.myapp.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.myapp.taskmanager.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController                         // @Controller + @ResponseBody (responde JSON)
@RequestMapping("/api/v1/tasks")      // prefijo para todas las rutas de este contolador
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET /api/v1/tasks/user/1?page=0&size=10&sort=createdAt,desc
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal User currentUser

    ) {
        if(!currentUser.getId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Valores por defecto si el cliente no manda los parámetros
        return ResponseEntity.ok(taskService.getTasksByUser(userId, pageable));
    }

    // GET /api/v1/tasks/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurityService.isOwner(#id, authentication.principal.id)")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // GET /api/v1/tasks
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
        // ResponseEntity = control total sobre la respuesta HTTP (status, headers, body)
    }

    // POST /api/v1/tasks
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO requestDTO
    ) {
        // @Valid dispara las validaciones de @NotBlank, @Size, etc
        // Si algo falla, Spring devuelve 400 automaticamente antes de llegar al Service
        TaskResponseDTO created = taskService.createTask(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    // PUT /api/v1/tasks/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurityService.isOwner(#id, authentication.principal.id)")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO requestDTO) {
        TaskResponseDTO updated = taskService.updateTask(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build(); // 204
        // si no existe -> misma cadena automatica
    }
}