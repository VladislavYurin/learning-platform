package ru.mentor.exception;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentor.config.CommonFeignConfig.CustomErrorDecoder.FeignClientExceptionWithResponse;

/**
 * Глобальный обработчик исключений.
 * <p>
 *      Маппит исключение {@link FeignClientExceptionWithResponse}, возникающее при
 *      ошибках вызовов внешних сервисов через Feign, в HTTP-ответ текущего сервиса,
 *      сохраняет исходный HTTP-статус и тело ответа внешнего сервиса.
 *      Позволяет прозрачно проксировать ошибку (pass-through) для клиента.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Преобразует {@link FeignClientExceptionWithResponse} в HTTP-ответ.
     * @param ex исключение со статусом и телом ответа внешнего сервиса
     * @return исходный статус внешнего сервиса и его тело в формате JSON
     */
    @ExceptionHandler(FeignClientExceptionWithResponse.class)
    public ResponseEntity<String> handleFeignClientExceptionWithResponse(
            FeignClientExceptionWithResponse ex) {

        return ResponseEntity.status(ex.getStatus())
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ex.getBody());
    }

}
