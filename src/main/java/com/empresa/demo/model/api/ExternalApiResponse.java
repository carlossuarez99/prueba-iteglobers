package com.empresa.demo.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ExternalApiResponse {
    private String userId;
    private Integer id;
    private String title;
    private String body;
}
