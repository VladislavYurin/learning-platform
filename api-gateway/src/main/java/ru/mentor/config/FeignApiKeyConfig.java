package ru.mentor.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс Feign для подстановки API-ключей в исходящие запросы.
 * Регистрирует интерсептор, который добавляет заголовок в
 * HTTP-запросы конкретного Feign-клиента.
 */
@Configuration
public class FeignApiKeyConfig {

    /**
     * API-ключ для Course Service.
     */
    @Value("${integration.course-service.api-key}")
    private String courseServiceApiKey;

    /**
     * API-ключ для Access Service.
     */
    @Value("${integration.access-service.api-key}")
    private String accessServiceApiKey;

    /**
     * Создает интерсептор, который добавляет заголовок в запросы Feign-клиента.
     * @return интерсептор с API-ключом в заголовке
     */
    @Bean
    public RequestInterceptor courseServiceApiKeyInterceptor() {
        return template -> {
            if (template.feignTarget().name().equals("courseClient")) {
                template.header("X-Service-Auth", courseServiceApiKey);
            }
        };
    }

    /**
     * Создает интерсептор, который добавляет заголовок с API-ключом в запросы Feign-клиента.
     * @return интерсептор с API-ключом в заголовке
     */
    @Bean
    public RequestInterceptor accessServiceApiKeyInterceptor() {
        return template -> {
            if (template.feignTarget().name().equals("mentorClient")) {
                template.header("X-Service-Auth", accessServiceApiKey);
            }
        };
    }

}
