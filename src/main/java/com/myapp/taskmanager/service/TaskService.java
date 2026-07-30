package com.myapp.taskmanager.service;

import com.myapp.taskmanager.dto.request.TaskRequestDTO;
import com.myapp.taskmanager.dto.response.TaskResponseDTO;
import com.myapp.taskmanager.entity.Task;
import com.myapp.taskmanager.exception.TaskNotFoundException;
import com.myapp.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // Bean de logica de negocio
public class TaskService {

    private final TaskRepository taskRepository;

    // Inyeccion por constructor (recomendada sobre @Autowired en campo)
    // Spring detecta que necesita un TaskRepository y lo inyecta automaticamente
    @Autowired
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<TaskResponseDTO> getAllTasks(){
        return taskRepository.findAll()
                .stream()
                .map(this::toResponseDTO) // convierte cada Task a TaskResponseDTO
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return toResponseDTO(task);

        /* Esto cuando se usa un "Optional"
        return taskRepository.findById(id)
                .map(this::toResponseDTO); // si existe, convertí, si no. Optional
        */
    }

    public TaskResponseDTO createTask(TaskRequestDTO requestDTO){
        // Aqui va la validacion de negocio, logica compleja, etc
        /* return taskRepository.save(task); */

        Task task = toEntity(requestDTO); // DTO -> Entity
        Task savedTask = taskRepository.save(task); // persiste en BD
        return toResponseDTO(savedTask); // Entity -> DTO para la respuesta
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO){
        // Si no existe, lanza "TaskNotFoundException" -> handler 404
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        existing.setTitle(requestDTO.getTitle());
        existing.setDescription(requestDTO.getDescription());
        existing.setCompleted(requestDTO.isCompleted());

        return toResponseDTO(taskRepository.save(existing));

        /* Esto cuando se usa un "Optional"
        return taskRepository.findById(id).map(existing -> {
            existing.setTitle(requestDTO.getTitle());
            existing.setDescription(requestDTO.getDescription());
            existing.setCompleted(requestDTO.isCompleted());
            return toResponseDTO(taskRepository.save(existing));
        });
        // .map() en Optional: si existe, transformalo; si no, retorna Optional.empty()
        */
    }

    public void deleteTask(Long id){
        taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);
    }

    // METODOS DE MAPPING PRIVADOS

    // Convierte Entity -> ResponseDTO (para enviar al cliente)
    private TaskResponseDTO toResponseDTO(Task task){
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }

    // Convierte RequestDTO -> Entity (para guardar en BD)
    private Task toEntity(TaskRequestDTO dto){
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());
        return task;
        // El id y createdAt los maneja JPA automaticamente
    }
}
