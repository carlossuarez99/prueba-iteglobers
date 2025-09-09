package com.empresa.demo.exeptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserException extends RuntimeException{

    private final int status;

    public UserException(String message, int status) {
        super(message);
        this.status = status;

    }

    public UserException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }
}
