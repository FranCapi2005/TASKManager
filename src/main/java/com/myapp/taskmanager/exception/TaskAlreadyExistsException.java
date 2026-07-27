package com.myapp.taskmanager.exception;

public class TaskAlreadyExistsException extends RuntimeException{

    public TaskAlreadyExistsException(String title) {
        super("Already exists a task with title: " + title);
    }
}
