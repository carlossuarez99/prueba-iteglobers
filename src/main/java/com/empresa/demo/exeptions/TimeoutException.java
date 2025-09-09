package com.empresa.demo.exeptions;

public class TimeoutException  extends RuntimeException{
    public TimeoutException(String message) {
        super(message);
    }
}
