package com.microservices.AdminService.exception;

public class CardNotFoundException extends RuntimeException{

    private static final long seriesVersionUID = 2;

    public CardNotFoundException(String message){super(message);}


}
