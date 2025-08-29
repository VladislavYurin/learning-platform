package ru.mentor.config;

import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.charset.StandardCharsets;

/**
 * Конфигурация для клиента API Telegram.
 *
 * Настраивает обработчик ошибок для взаимодействия с API Telegram
 * через Feign-клиент. При возникновении ошибки он будет логировать подробности
 * о запросах и ответах.
 */
@Slf4j
@Configuration
public class TelegramApiConfig {

    /**
     * Бин для настройки обработки ошибок API Telegram.
     * @return объект, реализующий интерфейс ErrorDecoder.
     */
    @Bean
    public ErrorDecoder telegramErrorDecoder() {
        return (String methodKey, Response response) -> {
            String body = null;
            try {
                if (response.body() != null) {
                    body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.warn("Не удалось прочитать тело ошибки Телеграм АПИ", e);
            }

            log.error("Телеграм АПИ ошибка: ключ метода = {}, статус = {}, причина = {}, тело = {}",
                    methodKey, response.status(), response.reason(), body);

            return FeignException.errorStatus(methodKey, response);
        };
    }
}
