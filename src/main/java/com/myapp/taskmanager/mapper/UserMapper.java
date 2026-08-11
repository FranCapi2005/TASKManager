package com.myapp.taskmanager.mapper;

import com.myapp.taskmanager.dto.request.UserRequestDTO;
import com.myapp.taskmanager.dto.response.UserResponseDTO;
import com.myapp.taskmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void updateEntityFromDTO(UserRequestDTO dto, @MappingTarget User entity);
}
