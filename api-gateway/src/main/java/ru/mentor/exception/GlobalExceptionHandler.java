package ru.mentor.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentor.config.CommonFeignConfig.CustomErrorDecoder.FeignClientExceptionWithResponse;
import ru.mentor.util.RqGenerator;

/**
 * Глобальный обработчик исключений.
 * <p>
 * Маппит исключение {@link FeignClientExceptionWithResponse}, возникающее при
 * ошибках вызовов внешних сервисов через Feign, в HTTP-ответ текущего сервиса,
 * сохраняет исходный HTTP-статус и тело ответа внешнего сервиса.
 * Позволяет прозрачно проксировать ошибку (pass-through) для клиента.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Преобразует {@link FeignClientExceptionWithResponse} в HTTP-ответ.
     *
     * @param ex
     *         исключение со статусом и телом ответа внешнего сервиса
     *
     * @return исходный статус внешнего сервиса и его тело в формате JSON
     */
    @ExceptionHandler(FeignClientExceptionWithResponse.class)
    public ResponseEntity<String> handleFeignClientExceptionWithResponse(
            FeignClientExceptionWithResponse ex) {

        return ResponseEntity.status(ex.getStatus())
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ex.getBody());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException e) {

        String rqId = RqGenerator.generateRqId();
        log.error("[requestId = {} ] Ошибка при попытке создания курса", rqId, e);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Доступ запрещен: у вас недостаточно прав для выполнения этой операции"
        );
        problem.setTitle("Access Denied");
        problem.setProperty("errorCode", "ACCESS_DENIED");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleInternalServerError(Exception e) {
        String rqId = RqGenerator.generateRqId();
        log.error("[requestId = {} ] Ошибка при получении списка тегов курса", rqId, e);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Произошла внутренняя ошибка сервера");
        problem.setProperty("requestId", rqId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

}
