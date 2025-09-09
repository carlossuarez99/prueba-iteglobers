package com.empresa.demo.controller.handlerexeption;

import com.empresa.demo.exeptions.DiscountCalculationException;
import com.empresa.demo.exeptions.RestClientExeption;
import com.empresa.demo.exeptions.TimeoutException;
import com.empresa.demo.exeptions.UserException;
import com.empresa.demo.model.generic.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscountCalculationException.class)
    public ResponseEntity<ApiResponse<String>> handleDiscountCalculationException(DiscountCalculationException ex) {
        log.error("Error de cálculo de descuento: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error("Cálculo de descuento fallido", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<String>> handleUserException(UserException ex) {
        log.error("Error de usuario: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error("Error de usuario", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponse<String>> handleRestApiExeption(RestClientException ex) {
        log.error("Error de consumo API externa: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error("Error al consumir la API externa", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<String>> handleRestApiExeption(TimeoutException ex) {
        log.error("Error de consumo API externa: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error("Error Timeout", ex.getMessage());
        return ResponseEntity.status(408).body(response);
    }

    @ExceptionHandler(RestClientExeption.class)
    public ResponseEntity<ApiResponse<String>> handleRestApiExeption(RestClientExeption ex) {
        log.error("Error de consumo API externa: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error("Error al consumir la API externa", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error("Error interno del servidor", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
