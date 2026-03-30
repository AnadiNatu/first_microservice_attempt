package com.microservices.AdminService.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final long seriesVersionUID = 1;

    public ResourceNotFoundException(String message){super(message);}

}
