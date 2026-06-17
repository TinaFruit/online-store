package com.example.springproject.exeption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HanderException {

    @ExceptionHandler(UpdatedFailed.class)
    public ResponseEntity<?> updatedFailed(UpdatedFailed up){
        return ResponseEntity.status(up.getCode()).body(up.getMessage());
    }

    @ExceptionHandler(DuplicateRegisterException.class)
    public ResponseEntity<?> duplicatedExp(DuplicateRegisterException dr){
        return ResponseEntity.status(dr.getCode()).body(dr.getMessage());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> AppException(AppException ap){
        return ResponseEntity.status(ap.getCode()).body(ap.getMessage());
    }

}
