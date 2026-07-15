package com.saumya.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().add("X-Internal-Api-Key", internalApiKey);
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}
