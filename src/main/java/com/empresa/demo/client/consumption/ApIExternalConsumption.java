package com.empresa.demo.client.consumption;

import com.empresa.demo.client.gateway.ApIExternalGateway;
import com.empresa.demo.client.model.ApiResponseDTO;
import com.empresa.demo.exeptions.RestClientExeption;
import com.empresa.demo.exeptions.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class ApIExternalConsumption implements ApIExternalGateway {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;

    @Value("${api.external.base-url}")
    public String url;


    @Override
    public List<ApiResponseDTO> getDataFromApi() {
        log.info("Iniciando la llamada a la API externa: {}", url);


            return retryTemplate.execute(context -> {
                int attempt = context.getRetryCount() + 1;
                log.info("Intento {} de obtener datos desde: {}", attempt, url);

                try {
                    ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            ApiResponseDTO[].class
                    );

                    return handleHttpResponse(response, attempt);
                } catch (ResourceAccessException e) {
                    log.warn("Timeout en intento {}: {}", attempt, e.getMessage());
                    throw new TimeoutException("Timeout al conectar con la API externa");
                }catch (Exception ex){
                    throw mapErrorToRestClientException(ex, attempt);
                }
            });
    }

    private List<ApiResponseDTO> handleHttpResponse(ResponseEntity<ApiResponseDTO[]> response, int attempt) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Datos obtenidos exitosamente en el intento {}", attempt);
            return Arrays.asList(response.getBody());
        } else if (response.getStatusCode().is4xxClientError()) {
            log.error("Error 4xx en intento {}: {}", attempt, response.getStatusCode());
            throw new RestClientExeption(response.getStatusCode().value(),
                    "Petición inválida a la API externa");
        } else if (response.getStatusCode().is5xxServerError()) {
            log.error("Error 5xx en intento {}: {}", attempt, response.getStatusCode());
            throw new RestClientExeption(response.getStatusCode().value(),
                    "Error del servidor en API externa");
        } else {
            log.error("Código HTTP inesperado {} en intento {}", response.getStatusCode(), attempt);
            throw new RestClientExeption(response.getStatusCode().value(), "Código HTTP inesperado: ");
        }
    }

    private RestClientExeption mapErrorToRestClientException(Exception e, int attempt) {
        if (e instanceof HttpClientErrorException httpClientError) {
            log.error("Error 4xx en intento {}: {}", attempt, httpClientError.getStatusCode());
            return new RestClientExeption(httpClientError.getStatusCode().value(),
                    "Petición inválida a la API externa: " + httpClientError.getMessage());
        } else if (e instanceof HttpServerErrorException httpServerError) {
            log.error("Error 5xx en intento {}: {}", attempt, httpServerError.getStatusCode());
            return new RestClientExeption(httpServerError.getStatusCode().value(),
                    "Error del servidor en API externa: " + httpServerError.getMessage());
        } else if (e instanceof TimeoutException) {
            log.error("Timeout en intento {}: {}", attempt, e.getMessage());
            return new RestClientExeption(408, "Timeout al conectar con la API externa: " + e.getMessage());
        } else {
            log.error("Error inesperado en intento {}: {}", attempt, e.getMessage());
            return new RestClientExeption(500, "Error inesperado: " + e.getMessage());
        }
    }
}

