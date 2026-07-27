package com.myapp.taskmanager.repository;

import com.myapp.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/*
@Repository // Bean de acceso a datos
public class TaskRepository {

    // Simulamos una base de datos en memoria por ahora
    // Cuando agreguemos JPA, esto desaparece y Spring lo genera automaticamente
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(); // thread-safe para IDs

    public List<Task> findAll(){
        return tasks;
    }

    public Optional<Task> findById(Long id){
        // Optional: forma Java de manejar "puede existir o no"
        // Evita NullPointerException, similar a null checks pero mas expresivo
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public Task save(Task task){
        if(task.getId() == null){
            task.setId(counter.incrementAndGet()); // nuevo id
        }else{
            tasks.removeIf(t -> t.getId().equals(task.getId())); // update
        }
        tasks.add(task);
        return task;
    }

    public void deleteById(Long id){
        tasks.removeIf(t -> t.getId().equals(id));
    }
}*/

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    // JpaRepository<TipoEntidad, TipoDeLaClavePrimaria>
    // Spring genera la implementacion completa en tiempo de ejecucion
    // Ya se tiene gratis: findAll(), findById(), save(), delete(), count(), etc.

    // Se puede agregar queries propias usando convencion de nombres:
    List<Task> findByCompleted(boolean completed);
    // Spring lee "findByCompleted" y genera: SELECT * FROM tasks WHERE completed

    List<Task> findByTitleContainingIgnoreCase(String keyboard);
    // SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%keyword%')
}