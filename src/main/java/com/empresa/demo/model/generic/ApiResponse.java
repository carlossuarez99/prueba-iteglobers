package com.empresa.demo.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private String status;
    private T data;
    private String error;
    private String detail;


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null, null);
    }

    public static <T> ApiResponse<T> error(String error, String detail) {
        return new ApiResponse<>("error", null, error, detail);
    }
}
