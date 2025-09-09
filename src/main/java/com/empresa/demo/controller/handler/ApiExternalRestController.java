package com.empresa.demo.controller.handler;

import com.empresa.demo.model.api.ExternalApiResponse;
import com.empresa.demo.model.generic.ApiResponse;
import com.empresa.demo.service.ExternalApiRest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
public class ApiExternalRestController {

    private final ExternalApiRest externalApiRest;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ExternalApiResponse>>> getUser() {
        var apiResponses = externalApiRest.getDataExternalApi();
        ApiResponse<List<ExternalApiResponse>> response = ApiResponse.success(apiResponses);
        return ResponseEntity.ok(response);
    }

}
