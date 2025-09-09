package com.empresa.demo.client.gateway;

import com.empresa.demo.client.model.ApiResponseDTO;

import java.util.List;

public interface ApIExternalGateway {

    List<ApiResponseDTO> getDataFromApi();
}
