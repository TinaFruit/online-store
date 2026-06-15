package com.example.springproject.exeption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HaddlerException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> ex(RuntimeException e){
        return ResponseEntity.status(404).body("failed: "+ e.getMessage());
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> ex(AppException e){
        return ResponseEntity.status(e.getCode()).body("failed: "+ e.getMessage());
    }
}
