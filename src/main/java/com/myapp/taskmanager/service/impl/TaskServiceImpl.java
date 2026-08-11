package com.myapp.taskmanager.service.impl;

import com.myapp.taskmanager.dto.request.TaskRequestDTO;
import com.myapp.taskmanager.dto.response.TaskResponseDTO;
import com.myapp.taskmanager.entity.Task;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.exception.TaskNotFoundException;
import com.myapp.taskmanager.exception.UserNotFoundException;
import com.myapp.taskmanager.mapper.TaskMapper;
import com.myapp.taskmanager.repository.TaskRepository;
import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service //Spring registra esta clase como "BEAN" que implementa TaskService
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    // Esta clase cumple con el contrato de "TaskService"
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;    // Necesitamos Buscar el Usuario
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    // Traer tareas de un usuario con paginacion
    @Override
    public Page<TaskResponseDTO> getTasksByUser(Long userId, Pageable pageable){
        // Verificamos que el usuario existe
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        // Page<Task> -> Page<TaskResponseDTO>
        // .map en Page funciona exactamente igual que en Stream, transforma cada elemento
        return taskRepository.findByUserId(userId, pageable)
                .map(taskMapper::toResponseDTO);
    }

    @Override  // indica que este método viene de la interface
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toResponseDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        // Buscamos el usuario antes de crear la tarea
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDTO.getUserId()));

        Task task = taskMapper.toEntity(requestDTO);
        task.setUser(user);
        return taskMapper.toResponseDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.updateEntityFromDTO(requestDTO, existing);
        return taskMapper.toResponseDTO(taskRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);
    }
}