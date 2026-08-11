package com.myapp.taskmanager.service;

import com.myapp.taskmanager.dto.request.UserRequestDTO;
import com.myapp.taskmanager.dto.response.UserResponseDTO;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.exception.UserNotFoundException;
import com.myapp.taskmanager.mapper.UserMapper;
import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setEmail("juan@test.com");
        user.setPassword("hashedpassword");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("Juan");
        userResponseDTO.setEmail("juan@test.com");

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Juan");
        userRequestDTO.setEmail("juan@test.com");
        userRequestDTO.setPassword("hashedpassword");
    }

    @Test
    @DisplayName("getAllUsers -> retorna lista de DTOs correctamente")
    void getAllUsers_ReturnListOfDTOs(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("juan@test.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getUserById -> usuario existe -> retorna DTO")
    void getUserById_WhenUserExists_ReturnDTO(){
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(userMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("createUser -> email disponible -> crea usuario correctamente")
    void createUser_WhenEmailAvailable_CreateUser(){
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("createUser -> email ya registrado -> lanza excepcion")
    void createUser_WhenEmailTaken_ThrowsException(){
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(userRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("juan@test.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteUser -> usuario no existe -> lanza excepcion")
    void deleteUser_WhenUserNotExists_ThrowsException(){
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRepository.deleteById(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }
}
