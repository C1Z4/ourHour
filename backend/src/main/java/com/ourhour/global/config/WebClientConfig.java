package com.ourhour.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${python-server.url}")
    private String pythonServerUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(pythonServerUrl)
                .build();
    }
}
