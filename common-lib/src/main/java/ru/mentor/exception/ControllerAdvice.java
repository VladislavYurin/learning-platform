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
     *
     * @param e перехваченное исключение EntityNotFoundException
     * @return ResponseEntity со статусом 404 и сообщением об ошибке в теле ответа
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение CustomAccessDeniedException.
     * Возвращает HTTP статус 403 (FORBIDDEN).
     *
     * @param e перехваченное исключение CustomAccessDeniedException
     * @return ResponseEntity со статусом 403 и сообщением об ошибке в теле ответа
     */
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<String> handleCustomAccessDeniedException(CustomAccessDeniedException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение AccessDeniedException.
     * Возвращает HTTP статус 403 (FORBIDDEN) с фиксированным сообщением.
     *
     * @param e перехваченное исключение AccessDeniedException
     * @return ResponseEntity со статусом 403 и сообщением "Отказано в доступе" в теле ответа
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Отказано в доступе");
    }

    /**
     * Обрабатывает исключение EntityAlreadyExistsException.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     *
     * @param e перехваченное исключение EntityAlreadyExistsException
     * @return ResponseEntity со статусом 400 и сообщением об ошибке в теле ответа
     */
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение FileProcessingException.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     *
     * @param e перехваченное исключение FileProcessingException
     * @return ResponseEntity со статусом 400 и сообщением об ошибке в теле ответа
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleConversionException(FileProcessingException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Обрабатывает исключение UserException.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     *
     * @param e перехваченное исключение UserException
     * @return ResponseEntity со статусом 400 и сообщением об ошибке в теле ответа
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
     *
     * @param ex перехваченное исключение ConstraintViolationException
     * @return ResponseEntity со статусом 400 и списком ошибок валидации в теле ответа
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
     *
     * @param e перехваченное исключение MethodArgumentNotValidException
     * @return ResponseEntity со статусом 400 и сообщением об ошибке в теле ответа
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleConstraintViolation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(TimeSlotUnavailableException.class)
    public ResponseEntity<String> handleTimeSlotUnavailableException(TimeSlotUnavailableException e) {
        log.info(String.format("[ RqUId = %s ] %s", e.getRqUId(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
