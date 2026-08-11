package com.myapp.taskmanager.service.impl;

import com.myapp.taskmanager.dto.request.UserRequestDTO;
import com.myapp.taskmanager.dto.response.UserResponseDTO;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.exception.UserAlreadyExistsException;
import com.myapp.taskmanager.exception.UserNotFoundException;
import com.myapp.taskmanager.mapper.UserMapper;
import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)     // Todos los metodos son "readOnly" por defecto
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        // Hereda "@Transactional(readOnly = true) de la clase, no necesita anotacion
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional // Reescribimos la transaccion a modo de uso y no lectura
    public UserResponseDTO createUser(UserRequestDTO requestDTO){
        // Verificamos que el mail no este usado antes de crear la cuenta
        if(userRepository.existsByEmail(requestDTO.getEmail())){
            throw new RuntimeException("El email ya esta en uso");
        }
        User user = userMapper.toEntity(requestDTO);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntityFromDTO(requestDTO, existing);
        return userMapper.toResponseDTO(userRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
