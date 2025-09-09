package com.empresa.demo.util;

import com.empresa.demo.exeptions.DiscountCalculationException;

public class ValidationUtils {

    private ValidationUtils(){}

    /**
     * Metodo verificacion de nulos sobre un objeto
     * @param value
     * @param errorMessage
     */
    public static void validateNotNull(Object value, String errorMessage, int status) {
        if (value == null) {
            throw new DiscountCalculationException(errorMessage, status);
        }
    }
}
