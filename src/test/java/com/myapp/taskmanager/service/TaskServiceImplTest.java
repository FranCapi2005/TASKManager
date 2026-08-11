package com.myapp.taskmanager.service;

import com.myapp.taskmanager.dto.request.TaskRequestDTO;
import com.myapp.taskmanager.dto.response.TaskResponseDTO;
import com.myapp.taskmanager.entity.Task;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.exception.TaskNotFoundException;
import com.myapp.taskmanager.mapper.TaskMapper;
import com.myapp.taskmanager.repository.TaskRepository;
import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)     // Activa Mockito en JUnit 5
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;  // Mock, no toca DB

    @Mock
    private UserRepository userRepository;  // mock

    @Mock
    private TaskMapper taskMapper;  // Mock

    @InjectMocks
    private TaskServiceImpl taskService;
    // InjectMocks crea una instancia REAL de TaskServiceImpl
    // e inyecta los @Mocks automaticamente

    // Variables reutilizables en este Test
    private Task task;
    private User user;
    private TaskResponseDTO taskResponseDTO;


    @BeforeEach // Se ejecuta antes de CADA test
    void SetUp(){
        user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setEmail("juan@test.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Tarea de Prueba");
        task.setCompleted(false);
        task.setUser(user);

        taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(1L);
        taskResponseDTO.setTitle("Tarea de Prueba");
        taskResponseDTO.setCompleted(false);
        taskResponseDTO.setUserId(1L);
        taskResponseDTO.setUserName("Juan");
    }

    @Test
    @DisplayName("getTaskById -> tarea existe -> retorna DTO correctamente")
    void getTaskById_WhenTaskExists_ReturnsDTO(){
        // ARRANGE -> prepara el escenario
        // "cuando llamen a findById(1L), devolve esta task"
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

        // ACT -> ejecutar lo que queremos testear
        TaskResponseDTO result = taskService.getTaskById(1L);

        // ASSERT -> verificar el resultado
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Tarea de Prueba");

        // verificar que se llamo al repository exactamente una vez
        verify(taskRepository, times(1)).findById(1L);
        verify(taskMapper, times(1)).toResponseDTO(task);
    }

    @Test
    @DisplayName("getTaskById -> tarea no existe -> lanza TaskNotFoundException")
    void getTaskById_WhenTaskNotExists_ThrowException(){
        // ARRANGE
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT -> verifica que lanza la excepcion correcta
        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");

        verify(taskRepository, times(1)).findById(99L);
        verify(taskMapper, never()).toResponseDTO(any());   // Nunca deberia llamarse
    }

    @Test
    @DisplayName("createTask -> usuario existe -> crea y retorna tarea")
    void createTask_WhenUserExists_CreateTask(){
        // ARRANGE
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Nueva tarea");
        requestDTO.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskMapper.toEntity(requestDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

        // ACT
        TaskResponseDTO result = taskService.createTask(requestDTO);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("deleteTask -> tarea no existe -> lanza TaskNotFoundException")
    void deleteTask_WhenTaskNotExists_ThrowException(){
        // ARRANGE
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT & ACT
        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).deleteById(any());
    }
}
