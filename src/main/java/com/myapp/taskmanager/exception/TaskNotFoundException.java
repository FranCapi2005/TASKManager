package com.myapp.taskmanager.exception;

// Extendemos RuntimeException -> excepcion no verificada
// En Java hay dos tipos:
// - Checked (extends Exception): Java te OBLIGA a manejarla con try/catch
// - Unchecked (extends RuntimeException): opcional manejarla, Spring las intercepta solo
public class TaskNotFoundException extends RuntimeException{

    private final Long taskId;

    public TaskNotFoundException(Long taskId){
        super("Task not found with ID: " + taskId); // mensaje del error
        this.taskId = taskId;
    }

    public Long getTaskId(){
        return taskId;
    }
}
