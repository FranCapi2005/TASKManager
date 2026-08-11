package com.myapp.taskmanager.service;

import com.myapp.taskmanager.dto.request.TaskRequestDTO;
import com.myapp.taskmanager.dto.response.TaskResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

// Una interface solo define QUÉ se puede hacer, no CÓMO
// Es un contrato, "cualquier cosa que implemente esto, tiene estos metodos"
public interface TaskService {
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO getTaskById(Long id);
    Page<TaskResponseDTO> getTasksByUser(Long userId, Pageable pageable);
    TaskResponseDTO createTask(TaskRequestDTO task);
    TaskResponseDTO updateTask(Long id, TaskRequestDTO task);
    void deleteTask(Long id);
}