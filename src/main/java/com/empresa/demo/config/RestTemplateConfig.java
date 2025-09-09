package com.empresa.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;



@Configuration
public class RestTemplateConfig {

    @Value("${api.external.timeout:2000}")
    private int timeout;

        @Bean
        public RestTemplate restTemplate() {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(timeout);
            factory.setReadTimeout(timeout);
            return new RestTemplate(factory);
        }

}
