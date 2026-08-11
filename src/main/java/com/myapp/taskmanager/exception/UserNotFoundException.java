package com.myapp.taskmanager.exception;

public class UserNotFoundException extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(Long userId){
        super("User not found with ID: " + userId); // mensaje del error
        this.userId = userId;
    }

    public Long getUserId(){
        return userId;
    }
}
