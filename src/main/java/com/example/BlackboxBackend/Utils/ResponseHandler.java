package com.example.BlackboxBackend.Utils;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
class RepsonseDTO <T>{
    private String message;
    private T data;
    private HttpStatus httpStatus;
}

public class ResponseHandler {
    public static <T> ResponseEntity<?> handleResponse(HttpStatus httpStatus, T data, String message){
        return ResponseEntity.status(httpStatus).body(new RepsonseDTO(message, data, httpStatus));
    }
}
