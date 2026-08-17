package com.myapp.taskmanager.security.service;

import com.myapp.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("taskSecurityService")
public class TaskSecurityService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskSecurityService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // verifica si el usuario tiene permisos para acceder a una tarea
    public void isOwner(Long taskId, Long userId){
        taskRepository.findById(taskId)
                .map(task -> task.getUser().getId().equals(userId))
                .orElse(false);
        // si la tarea no existe, retorna false
        // El 404 lo maneja el Service cuando realmente se intenta acceder
    }
}
