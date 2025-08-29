package ru.mentor.config;

import feign.Client;
import feign.FeignException;
import feign.Response;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StreamUtils;

/**
 * Конфигурационный класс для клиентов OpenFeign в приложении.
 * Класс используется для настройки поведения Feign-клиентов: уровня логирования,
 * таймаутов, политики повторов, перехватчиков запросов, обработки ошибок через ErrorDecoder
 * и кодеков сериализации.
 */
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

    /**
     * Создает HTTP-клиент Feign по умолчанию.
     * @return экземпляр клиента
     * @throws RuntimeException если создание клиента завершилось ошибкой
     */
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
     * Декодер ошибок Feign.
     * @return экземпляр декодера
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * Класс для преобразования ответа с ошибкой (4хх/5хх) в исключение со статусом и телом ответа.
     * Если тело ответа прочитать не удалось, то возвращается стандартное исключение.
     */
    public static class CustomErrorDecoder implements ErrorDecoder {

        /**
         * Преобразует ошибочный HTTP-ответ в исключение.
         * @param methodKey идентификатор вызываемого метода Feign (интерфейс#метод)
         * @param response исходный HTTP-ответ от удаленного сервера
         * @return исключение, которое Feign выбрасывает взывающему коду,
         * поведение Feign по умолчанию {@code null}
         */
        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                return new FeignClientExceptionWithResponse(response);
            } catch (IOException ignored) {
            }
            return null;
        }

        /**
         * Исключение Feign со статусом и текстом тела ответа (читается как UTF-8),
         * при отсутствии тела вовращается пустая строка.
         */
        @Getter
        public static class FeignClientExceptionWithResponse extends FeignException {

            /**
             * HTTP-стутус ответа.
             */
            private final int status;

            /**
             * Тело ответа в формате UTF-8 либо пустая строка.
             */
            private final String body;

            /**
             * Конструктор исключения, сохраняет из ошибочного ответа Feign HTTP-статус и тело ответа
             * для дальнейшего логирования и обработки.
             * @param response исходный HTTP-ответ Feign
             * @throws IOException при ошибке чтения ответа
             */
            protected FeignClientExceptionWithResponse(Response response)
                    throws IOException {
                super(response.status(), "Feign client error", response.request());
                this.status = response.status();
                this.body = response.body() == null ? "" :
                        StreamUtils.copyToString(
                                response.body().asInputStream(),
                                StandardCharsets.UTF_8
                        );
            }

        }

    }

}
