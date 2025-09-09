package com.empresa.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableRetry
public class RetryConfig {

    @Value("${api.external.retry:2}")
    private Integer retry;

    @Value("${api.external.timeout}")
    private Long timeout;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Política de reintentos
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retry);
        retryTemplate.setRetryPolicy(retryPolicy);

        // Política de delay
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(timeout);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
