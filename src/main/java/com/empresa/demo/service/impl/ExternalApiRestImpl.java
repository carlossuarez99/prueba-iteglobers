package com.empresa.demo.service.impl;

import com.empresa.demo.client.gateway.ApIExternalGateway;
import com.empresa.demo.model.api.ExternalApiResponse;
import com.empresa.demo.service.ExternalApiRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiRestImpl implements ExternalApiRest {

    private final ApIExternalGateway apIExternalGateway;

    @Override
    public List<ExternalApiResponse> getDataExternalApi() {
        log.info("Iniciando la llamada a la API externa...");

        var resList = apIExternalGateway.getDataFromApi();

        log.info("Datos obtenidos de la API externa: {}", resList);

        // Mapear cada ApiResponseDTO a ExternalApiResponse usando Stream
        List<ExternalApiResponse> externalApiResponses = resList.stream()
                .map(res -> ExternalApiResponse.builder()
                        .body(res.body())
                        .id(res.id())
                        .title(res.title())
                        .userId(res.userId())
                        .build())
                .collect(Collectors.toList());

        log.info("Respuesta procesada exitosamente: {}", externalApiResponses);

        return externalApiResponses;
    }


}
