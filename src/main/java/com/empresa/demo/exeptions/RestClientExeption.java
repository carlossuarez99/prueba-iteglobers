package com.empresa.demo.exeptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestClientExeption  extends RuntimeException{

    private final int status;

    public RestClientExeption( int status, String message) {
        super(message);
        this.status = status;

    }

    public RestClientExeption(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }
}
