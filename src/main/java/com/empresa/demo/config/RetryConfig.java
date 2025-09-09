package com.empresa.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class RetryConfig {

    @Value("${api.external.retry:2}")
    private Integer retry;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retry);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}

