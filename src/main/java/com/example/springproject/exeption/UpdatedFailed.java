package com.example.springproject.exeption;

public class UpdatedFailed extends RuntimeException{
    private int code;
    public UpdatedFailed(int code, String message){
        super(message);
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
