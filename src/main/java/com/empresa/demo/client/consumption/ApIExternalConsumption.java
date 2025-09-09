package com.empresa.demo.client.consumption;

import com.empresa.demo.client.model.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@Component
public class ApIExternalConsumption {

    private final RestTemplate restTemplate;


    @Value("${api.external.base-url}")
    private String url;

    @Value("${api.external.retry:2}")
    private Integer retry;

    public ApiResponseDTO getDataFromApi() {
        return retryTemplate.execute(context -> {
            // Construir la URL de la API (por si hay parámetros de consulta)
            String apiUrl = UriComponentsBuilder.fromHttpUrl(url).toUriString();

            // Intentar obtener los datos desde la API
            return restTemplate.getForObject(apiUrl, ApiResponseDTO.class);
        });
    }
}
