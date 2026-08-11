package com.myapp.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // unique = UNIQUE constraint en DB
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    // Un usuario tiene "muchas" tareas
    // mappedBy = "user" -> le dice a JPA que la relacion ya esta mapeada
    //                      en el campo "user" de la clase Task
    // cascade = ALL -> si borras un usuario, se borran sus tareas tambien
    // orphanRemoval -> si una tarea se desconecta del usuario, se borra de la DB
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}
