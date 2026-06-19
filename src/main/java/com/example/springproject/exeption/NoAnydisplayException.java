package com.example.springproject.exeption;

public class NoAnydisplayException extends RuntimeException {
    private int code;

    public NoAnydisplayException(int code, String message) {
        super(message);
        this.code = code;

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
