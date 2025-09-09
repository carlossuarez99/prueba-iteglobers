package com.empresa.demo.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponseDTO(
        @JsonProperty("userId") String userId,
        @JsonProperty("id") Integer id,
        @JsonProperty("title") String title,
        @JsonProperty("body") String body
) { }
