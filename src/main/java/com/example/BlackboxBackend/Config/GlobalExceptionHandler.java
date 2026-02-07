package com.example.BlackboxBackend.Config;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArugmentInvalidException(Exception ex){
        return ResponseHandler.handleResponse(HttpStatus.CONFLICT, null, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArugmentTypeMismatchException(Exception ex){
        return ResponseHandler.handleResponse(HttpStatus.CONFLICT, null, ex.getMessage());
    }

    @ExceptionHandler(CustomError.class)
    public ResponseEntity<?> handleCustomException(CustomError ex){
        return ResponseHandler.handleResponse(ex.getHttpStatus(), null, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleCommonException(Exception ex){
        return ResponseHandler.handleResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
    }
}
