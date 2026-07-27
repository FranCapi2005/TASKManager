package com.myapp.taskmanager.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//// Lombok genera automaticamente getters, setters, equals, hashCode, toString
//@Data
//@NoArgsConstructor               // constructor vacio: Task()
//@AllArgsConstructor              // constructor completo: Task(id, title, completed)
//public class Task {
//    private Long id;
//    private String title;
//    private String description;
//    private boolean completed;
//}
//

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity                         // "esta clase representa una tabla en la BD"
@Table(name = "tasks")          // nombre explicito de la tabla
@Data
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

    // Se ejecuta automaticamente ANTES de persistir (INSERT) pro primera vez en la BD
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}