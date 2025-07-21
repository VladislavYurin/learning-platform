package ru.mentor.config;

import feign.Client;
import feign.Logger;
import feign.Response;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class CommonFeignConfig {

    /**
     * Параметр ожидания между запросами, который можно настроить через свойства приложения.
     */
    @Value("${feign.client.retry.period}")
    private long period;

    /**
     * Параметр максимального ожидания, который можно настроить через свойства
     * приложения.
     */
    @Value("${feign.client.retry.maxPeriod}")
    private long maxPeriod;

    /**
     * Количество повторных попыток, который можно настроить через свойства приложения.
     */
    @Value("${feign.client.retry.maxAttempts}")
    private int maxAttempts;

    @Bean
    public Client client() {
        try {
            return new Client.Default(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Feign client with SSL context", e);
        }
    }

    /**
     * Создает и возвращает пользовательский {@link Retryer} с заданными параметрами.
     *
     * @return экземпляр {@link Retryer} с настроенными параметрами повторных попыток
     */
    @Bean
    public Retryer customRetryer() {
        return new CustomRetryer(period, maxPeriod, maxAttempts);
    }

    /**
     * Пользовательский класс для реализации логики повторных попыток.
     * Этот класс расширяет {@link Retryer.Default} и использует параметры,
     * заданные в конфигурационном классе.
     */
    public static class CustomRetryer extends Retryer.Default {

        public CustomRetryer(long period, long maxPeriod, int maxAttempts) {
            super(period, maxPeriod, maxAttempts);
        }

    }

    /**
     * Создает и возвращает пользовательский {@link ErrorDecoder}.
     *
     * @return экземпляр {@link ErrorDecoder} с пользовательской логикой обработки ошибок
     */
    @Bean
    public ErrorDecoder customErrorDecoder() {
        return new CommonFeignConfig.CustomErrorDecoder();
    }

    /**
     * Пользовательский класс для обработки ошибок в ответах от сервера.
     * Этот класс расширяет {@link ErrorDecoder.Default} и добавляет логирование.
     */
    public static class CustomErrorDecoder extends ErrorDecoder.Default {

        /**
         * Декодирует ответ от сервера и возвращает исключение {@link RetryableException}
         * для ошибок в диапазоне 4xx и 5xx.
         *
         * @param methodKey
         *         ключ метода
         * @param response
         *         ответ от сервера
         *
         * @return исключение {@link RetryableException} для ошибок в диапазоне 4xx и 5xx
         */
        @Override
        public Exception decode(String methodKey, Response response) {
            String url = response.request().url();
            int status = response.status();
            String responseBody = extractResponseBody(response);
            HttpStatus.Series series = HttpStatus.Series.resolve(status);

            if (series == HttpStatus.Series.SERVER_ERROR) {
                log.error(
                        "Received server error response with status: {}, body = {}",
                        status,
                        responseBody
                );

                return new RetryableException(
                        response.status(),
                        "Received server error response executing POST " + url,
                        response.request().httpMethod(),
                        (Long) null,
                        response.request()
                );
            } else if (series == HttpStatus.Series.CLIENT_ERROR) {
                log.error(
                        "Received client error response with status: {}, body = {}",
                        status,
                        responseBody
                );

                return new Exception("Client error occurred. Status: " + status + ", URL: " + url);
            }

            return super.decode(methodKey, response);
        }

    }

    /**
     * Извлекает тело ответа и возвращает его в виде строки.
     *
     * @param response
     *         ответ от сервера
     *
     * @return тело ответа в виде строки
     */
    private static String extractResponseBody(Response response) {
        if (response.body() != null) {
            try {
                return IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("Error reading response body", e);
            }
        }

        return "No response body";
    }

    /**
     * Логирование запроса и ответа для всех клиентов. Необходимо перевести выбранный класс / пакет
     * с клиентом в режим DEBUG.
     *
     * @return конфигурация уровня логирования запросов
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.valueOf("DEBUG");
    }

}
