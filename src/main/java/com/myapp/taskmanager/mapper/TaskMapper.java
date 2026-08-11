package com.myapp.taskmanager.mapper;

import com.myapp.taskmanager.dto.request.TaskRequestDTO;
import com.myapp.taskmanager.dto.response.TaskResponseDTO;
import com.myapp.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
// componentModel = "spring" -> mapStruct usa spring para generar un @Component
// Spring lo detecta como Bean e inyectable con @Autowired
public interface TaskMapper {
    // Task -> TaskResponseDTO
    // Los campos con mismo nombre se mapean solos
    // Los que difieren se necesita @Mapping Explicito
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    TaskResponseDTO toResponseDTO(Task task);

    // TaskRequestDTO -> Task
    // userId no existe en Task como campo directo (es una relacion)
    // lo ignoramos aca y lo asignamos manualmente en el Service
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Task toEntity(TaskRequestDTO dto);

    // Actualiza una Entity existente con datos de un DTO
    // @MappingTarget -> indica cual es el objetivo que se va a modificar
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDTO(TaskRequestDTO dto, @MappingTarget Task entity);
}