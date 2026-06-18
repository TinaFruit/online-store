package com.example.springproject.exeption;

public class DuplicateRegisterException extends RuntimeException{
    private int code;

    public DuplicateRegisterException(int code, String message){
        super(message);
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
