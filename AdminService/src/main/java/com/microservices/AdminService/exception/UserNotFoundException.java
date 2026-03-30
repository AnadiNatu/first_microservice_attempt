package com.microservices.AdminService.exception;

public class UserNotFoundException extends RuntimeException{

    private static final long seriesVersionUID = 3;

    public UserNotFoundException(String message){super(message);}
}
