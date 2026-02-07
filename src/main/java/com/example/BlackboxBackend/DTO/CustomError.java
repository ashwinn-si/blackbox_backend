package com.example.BlackboxBackend.DTO;

import org.springframework.http.HttpStatus;

public class CustomError extends Exception{
    private HttpStatus httpStatus;
    public CustomError(HttpStatus httpStatus, String message){
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }
}
