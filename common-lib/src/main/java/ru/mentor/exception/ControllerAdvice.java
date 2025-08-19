package ru.mentor.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Перехватывает различные типы исключений и возвращает соответствующие HTTP ответы.
 */
@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    /**
     * Обрабатывает исключение EntityNotFoundException.
     * Возвращает HTTP статус 404 (NOT_FOUND).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение CustomAccessDeniedException.
     * Возвращает HTTP статус 403 (FORBIDDEN).
     */
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<String> handleCustomAccessDeniedException(CustomAccessDeniedException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение AccessDeniedException.
     * Возвращает HTTP статус 403 (FORBIDDEN) с фиксированным сообщением.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Отказано в доступе");
    }

    /**
     * Обрабатывает исключение EntityAlreadyExistsException.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     */
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение FileProcessingException.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleConversionException(FileProcessingException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение UserException.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserException(UserException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение ConstraintViolationException, возникающее при нарушении
     * ограничений валидации Jakarta Bean Validation.
     * Возвращает HTTP статус 400 (BAD_REQUEST) со списком всех нарушений.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                                .stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .toList();

        return ResponseEntity.badRequest()
                             .body(String.join("; ", errors));
    }

    /**
     * Обрабатывает исключение MethodArgumentNotValidException, возникающее при
     * нарушении ограничений валидации аргументов метода контроллера.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleConstraintViolation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
