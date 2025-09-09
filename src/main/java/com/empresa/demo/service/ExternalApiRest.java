package com.empresa.demo.service;

import com.empresa.demo.model.api.ExternalApiResponse;

import java.util.List;

public interface ExternalApiRest {
    List<ExternalApiResponse> getDataExternalApi();
}
