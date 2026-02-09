package ru.mentor.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentor.config.CommonFeignConfig.CustomErrorDecoder.FeignClientExceptionWithResponse;
import ru.mentor.exception.useravatar.UserAvatarServiceException;
import ru.mentor.exception.useravatar.UserAvatarValidationException;
import ru.mentor.util.RqGenerator;

import java.util.stream.Collectors;

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

    private static final String ACCESS_DENIED_PROBLEM_TITLE = "Access Denied";
    private static final String ENTITY_NOT_FOUND_PROBLEM_TITLE = "Entity not Found";
    private static final String ENTITY_ALREADY_EXISTS_PROBLEM_TITLE = "Entity already exists";
    private static final String TIME_SLOT_UNAVAILABLE = "TimeSlot unavailable";
    private static final String INTERNAL_SERVER_ERROR_PROBLEM_TITLE = "Internal Server Error";

    /**
     * Преобразует {@link FeignClientExceptionWithResponse} в HTTP-ответ.
     *
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException e) {

        String requestId = RqGenerator.generateRqId();
        log.error("[ requestId = {} ] {}", requestId, e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Доступ запрещен: у вас недостаточно прав для выполнения этой операции"
        );
        problem.setTitle(ACCESS_DENIED_PROBLEM_TITLE);
        problem.setProperty("errorCode", ACCESS_DENIED_PROBLEM_TITLE.toUpperCase());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleCustomAccessDeniedException(
            CustomAccessDeniedException e) {

        String requestId = e.getRequestId() != null ? e.getRequestId() : RqGenerator.generateRqId();
        log.error("[RequestUid = {} ] {}", requestId, e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
        problem.setTitle(ACCESS_DENIED_PROBLEM_TITLE);
        problem.setProperty("errorCode", ACCESS_DENIED_PROBLEM_TITLE.toUpperCase());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException e) {
        String requestId = e.getRequestId() != null ? e.getRequestId() : RqGenerator.generateRqId();
        log.info("[RequestUid = {} ] {}", requestId, e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        problem.setTitle(ENTITY_NOT_FOUND_PROBLEM_TITLE);
        problem.setProperty("errorCode", ENTITY_NOT_FOUND_PROBLEM_TITLE.toUpperCase());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        String requestId = e.getRequestId() != null ? e.getRequestId() : RqGenerator.generateRqId();

        log.info("[ RequestUid = {} ] {}", requestId, e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
        problem.setTitle(ENTITY_ALREADY_EXISTS_PROBLEM_TITLE);
        problem.setProperty("errorCode", ENTITY_ALREADY_EXISTS_PROBLEM_TITLE.toUpperCase());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(UserAvatarValidationException.class)
    public ResponseEntity<ProblemDetail> handleUserAvatarValidationException(
            UserAvatarValidationException e) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.warn("Ошибка валидации файла: {}", e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Ошибка валидации файла");
        problem.setDetail(e.getMessage());

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(UserAvatarServiceException.class)
    public ResponseEntity<ProblemDetail> handleUserAvatarServiceException(UserAvatarServiceException e) {

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        log.error("Ошибка при работе с хранилищем: {}", e.getMessage(), e);

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Ошибка получения файла");
        problem.setDetail("Не удалось получить файл");

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                message
        );
        problem.setTitle("Validation Error");
        problem.setProperty("errorCode", "VALIDATION_FAILED");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(TimeSlotUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleTimeSlotUnavailable(TimeSlotUnavailableException e) {

        String requestId = e.getRequestId() != null ? e.getRequestId() : RqGenerator.generateRqId();
        log.error("[RequestUid = {} ] {}", requestId, e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
        problem.setTitle(TIME_SLOT_UNAVAILABLE);
        problem.setProperty("errorCode", TIME_SLOT_UNAVAILABLE.toUpperCase());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleInternalServerError(Exception e) {
        String requestId = RqGenerator.generateRqId();
        log.error("[requestId = {} ] {}", requestId, e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle(INTERNAL_SERVER_ERROR_PROBLEM_TITLE);
        problem.setProperty("errorCode", INTERNAL_SERVER_ERROR_PROBLEM_TITLE.toUpperCase());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

}