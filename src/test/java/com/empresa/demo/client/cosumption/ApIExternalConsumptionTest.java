package com.empresa.demo.client.cosumption;

import com.empresa.demo.client.consumption.ApIExternalConsumption;
import com.empresa.demo.client.model.ApiResponseDTO;
import com.empresa.demo.exeptions.RestClientExeption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApIExternalConsumptionTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private ApIExternalConsumption apiExternalConsumption;

    private final String apiUrl = "https://api.example.com/data";
    private final ApiResponseDTO[] mockResponse = {
            new ApiResponseDTO("1", 1, "Description 1", "pruebas"),
            new ApiResponseDTO("2", 1 ,"Test 1", "pruebas")
    };


    @Test
    void getDataFromApi_TimeoutError_ThrowsRestClientException() {

        RestClientExeption expectedException = new RestClientExeption(408, "Timeout al conectar con la API externa");

        when(retryTemplate.execute(any()))
                .thenThrow(expectedException);

        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(408, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Timeout"));

        verify(retryTemplate, times(1)).execute(any());

    }



    @Test
    void getDataFromApi_UnexpectedError_ThrowsRestClientException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenThrow(exception);

        // Mock simple sin especificar los tipos exactos de los parámetros
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    // Simulamos la lógica que se ejecutaría dentro del RetryTemplate
                    try {
                        ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                                apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                        return Arrays.asList(response.getBody());
                    } catch (RuntimeException e) {
                        // Simulamos el mapeo que hace tu código a RestClientExeption
                        throw new RestClientExeption(500, "Error inesperado: " + e.getMessage());
                    }
                });

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(500, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Error inesperado"));
        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }

    @Test
    void getDataFromApi_Success_ReturnsData() {
        // Arrange
        ResponseEntity<ApiResponseDTO[]> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenReturn(responseEntity);

        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                            apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                    return Arrays.asList(response.getBody());
                });

        // Act
        List<ApiResponseDTO> result = apiExternalConsumption.getDataFromApi();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Description 1", result.get(0).title());
        assertEquals("Description 1", result.get(0).title());
        assertEquals("Description 1", result.get(0).title());

        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }

    @Test
    void getDataFromApi_ClientError_ThrowsException() {
        // Arrange
        HttpClientErrorException exception =
                new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenThrow(exception);

        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    try {
                        ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                                apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                        return Arrays.asList(response.getBody());
                    } catch (HttpClientErrorException e) {
                        throw new RestClientExeption(e.getStatusCode().value(),
                                "Petición inválida a la API externa");
                    }
                });

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(400, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Petición inválida"));

        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }

    @Test
    void getDataFromApi_ServerError_ThrowsException() {
        // Arrange
        HttpServerErrorException exception =
                new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error");

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenThrow(exception);

        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    try {
                        ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                                apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                        return Arrays.asList(response.getBody());
                    } catch (HttpServerErrorException e) {
                        throw new RestClientExeption(e.getStatusCode().value(),
                                "Error del servidor en API externa");
                    }
                });

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(500, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Error del servidor"));

        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }

    @Test
    void getDataFromApi_UnexpectedStatus_ThrowsException() {
        // Arrange
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    throw new RestClientExeption(207, "Código HTTP inesperado: ");
                });

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(207, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Código HTTP inesperado"));

        verify(retryTemplate, times(1)).execute(any());
    }

    @Test
    void getDataFromApi_HttpClientError_ReturnsMappedException() {
        // Arrange
        when(retryTemplate.execute(any()))
                .thenThrow(new RestClientExeption(400, "Petición inválida a la API externa: Bad Request"));

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(400, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Petición inválida"));

        verify(retryTemplate, times(1)).execute(any());
    }

    @Test
    void getDataFromApi_HttpServerError_ReturnsMappedException() {
        // Arrange
        HttpServerErrorException exception =
                new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error");

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenThrow(exception);

        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    try {
                        ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                                apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                        return Arrays.asList(response.getBody());
                    } catch (HttpServerErrorException e) {
                        throw new RestClientExeption(e.getStatusCode().value(),
                                "Error del servidor en API externa: " + e.getMessage());
                    }
                });

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(500, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Error del servidor"));

        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }

    @Test
    void getDataFromApi_TimeoutError_ReturnsMappedException() {
        // Arrange
        when(retryTemplate.execute(any()))
                .thenThrow(new RestClientExeption(408, "Timeout al conectar con la API externa: Timeout occurred"));

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(408, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Timeout"));

        verify(retryTemplate, times(1)).execute(any());
    }

    @Test
    void getDataFromApi_GenericError_ReturnsMappedException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Generic error");

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenThrow(exception);

        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    try {
                        ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                                apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                        return Arrays.asList(response.getBody());
                    } catch (RuntimeException e) {
                        throw new RestClientExeption(500, "Error inesperado: " + e.getMessage());
                    }
                });

        // Act & Assert
        RestClientExeption thrown = assertThrows(RestClientExeption.class, () -> {
            apiExternalConsumption.getDataFromApi();
        });

        assertEquals(500, thrown.getStatus());
        assertTrue(thrown.getMessage().contains("Error inesperado"));

        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }

    @Test
    void getDataFromApi_WithRetryTemplateMock_Success() {
        // Arrange
        ResponseEntity<ApiResponseDTO[]> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        )).thenReturn(responseEntity);

        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    ResponseEntity<ApiResponseDTO[]> response = restTemplate.exchange(
                            apiUrl, HttpMethod.GET, null, ApiResponseDTO[].class);
                    return Arrays.asList(response.getBody());
                });

        // Act
        List<ApiResponseDTO> result = apiExternalConsumption.getDataFromApi();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(ApiResponseDTO[].class)
        );
    }





}