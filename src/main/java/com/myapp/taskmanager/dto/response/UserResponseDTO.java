package com.myapp.taskmanager.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    // no incluimos la contraseña debido a que no queremos que se muestre junto a los demas datos
}
