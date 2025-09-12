package ru.mentor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки OpenAPI/Swagger для приложения.
 * <p>
 *     Документация доступна после запуска приложения по адресу {@code /v3/api-docs},
 *     интерактивная документация Swagger UI по адресу {@code /swagger-ui.html}.
 * </p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Создаёт основной объект OpenAPI с базовыми метаданными сервиса.
     * @return экземпляр OpenAPI с заданными {@code title}, {@code version} и {@code description}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                              .title("Learning Platform API")
                              .version("1.0")
                              .description("API для управления образовательной платформой"));
    }

}
