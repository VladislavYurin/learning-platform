package ru.mentor.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignApiKeyConfig {

    @Value("${integration.course-service.api-key}")
    private String courseServiceApiKey;

    @Value("${integration.access-service.api-key}")
    private String accessServiceApiKey;

    @Bean
    public RequestInterceptor courseServiceApiKeyInterceptor() {
        return template -> {
            if (template.feignTarget().name().equals("courseClient")) {
                template.header("X-Service-Auth", courseServiceApiKey);
            }
        };
    }

    @Bean
    public RequestInterceptor accessServiceApiKeyInterceptor() {
        return template -> {
            if (template.feignTarget().name().equals("accessClient")) {
                template.header("X-Service-Auth", accessServiceApiKey);
            }
        };
    }

}
