package com.myapp.taskmanager.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(int id) {
        super("Already exists a user with id: " + id);
    }
}
