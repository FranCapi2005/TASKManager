package com.myapp.taskmanager.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity                         // "esta clase representa una tabla en la BD"
@Table(name = "tasks")          // nombre explicito de la tabla
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // auto-increment
    private Long id;

    @Column(nullable = false)           // NOT NULL en la base de datos
    private String title;

    @Column(columnDefinition = "TEXT")  // columna de texto largo
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at")        // nombre explicito de la columna
    private LocalDateTime createdAt;

    // Muchas tareas perteneces a un usuario
    // @ManyToOne: el lado que tiene la foreing key en la tabla
    // @JoinColumn: nombre de la columna FK en la tabla tasks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // Hibernate genera: user_id BIGINT REFERENCES users(id)

    // Se ejecuta automaticamente ANTES de persistir (INSERT) pro primera vez en la BD
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}