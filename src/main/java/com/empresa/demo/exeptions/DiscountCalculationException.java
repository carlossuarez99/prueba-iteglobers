package com.empresa.demo.exeptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DiscountCalculationException extends RuntimeException{

    private final int status;
    public DiscountCalculationException(String message, int status) {
        super(message);
        this.status = status;

    }

    public DiscountCalculationException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }
}
