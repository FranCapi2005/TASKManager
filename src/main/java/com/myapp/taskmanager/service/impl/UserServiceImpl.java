package com.myapp.taskmanager.service.impl;

import com.myapp.taskmanager.dto.request.UserRequestDTO;
import com.myapp.taskmanager.dto.response.UserResponseDTO;
import com.myapp.taskmanager.entity.Role;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.exception.EmailAlreadyExistsException;
import com.myapp.taskmanager.exception.UserNotFoundException;
import com.myapp.taskmanager.mapper.UserMapper;
import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)     // Todos los metodos son "readOnly" por defecto
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(value = "users")
    // sin key explicita -> Spring usa los parametros del metodo como key
    // como no hay parametros -> key = "users::SimpleKey []"
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserResponseDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDTO createUser(UserRequestDTO requestDTO){
        if(userRepository.existsByEmail(requestDTO.getEmail())){
            throw new EmailAlreadyExistsException(requestDTO.getEmail());
        }
        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(requestDTO.getRole() != null ? requestDTO.getRole() : Role.USER);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "users", allEntries = true)
    })
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntityFromDTO(requestDTO, existing);
        existing.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        return userMapper.toResponseDTO(userRepository.save(existing));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "users", allEntries = true)
    })
    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
