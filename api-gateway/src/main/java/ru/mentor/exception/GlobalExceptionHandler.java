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
import ru.mentor.exception.useravatar.UserAvatarServiceException;
import ru.mentor.exception.useravatar.UserAvatarValidationException;

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
    private static final String ENTITY_NOT_FOUND_PROBLEM_TITLE = "Not Found";
    private static final String ENTITY_ALREADY_EXISTS_PROBLEM_TITLE = "Already exists";
    private static final String INTERNAL_SERVER_ERROR_PROBLEM_TITLE = "Internal Server Error";
    private static final String INVALID_REFRESH_TOKEN_PROBLEM_TITLE = "Invalid Refresh Token";

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

        log.error(
                "[exceptionType={}] [status={}] Ошибка при вызове внешнего сервиса через Feign.",
                ex.getClass().getSimpleName(),
                ex.getStatus(),
                ex
        );

        return ResponseEntity.status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getBody());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException e) {

        log.error(
                "[exceptionType={}] [errorCode=ACCESS_DENIED] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Доступ запрещен: у вас недостаточно прав для выполнения этой операции"
        );
        problem.setTitle(ACCESS_DENIED_PROBLEM_TITLE);
        problem.setProperty("errorCode", "ACCESS_DENIED");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleCustomAccessDeniedException(
            CustomAccessDeniedException e) {

        log.error(
                "[exceptionType={}] [errorCode=ACCESS_DENIED] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
        problem.setTitle(ACCESS_DENIED_PROBLEM_TITLE);
        problem.setProperty("errorCode", "ACCESS_DENIED");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException e) {

        log.error(
                "[exceptionType={}] [errorCode=ENTITY_NOT_FOUND] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        problem.setTitle(ENTITY_NOT_FOUND_PROBLEM_TITLE);
        problem.setProperty("errorCode", "ENTITY_NOT_FOUND");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {

        log.error(
                "[exceptionType={}] [errorCode=ENTITY_ALREADY_EXISTS] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
        problem.setTitle(ENTITY_ALREADY_EXISTS_PROBLEM_TITLE);
        problem.setProperty("errorCode", "ENTITY_ALREADY_EXISTS");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(UserAvatarValidationException.class)
    public ResponseEntity<ProblemDetail> handleUserAvatarValidationException(
            UserAvatarValidationException e) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.error(
                "[exceptionType={}] [errorCode=USER_AVATAR_VALIDATION] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Ошибка валидации файла");
        problem.setDetail(e.getMessage());

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(UserAvatarServiceException.class)
    public ResponseEntity<ProblemDetail> handleUserAvatarServiceException(UserAvatarServiceException e) {

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        log.error(
                "[exceptionType={}] [errorCode=USER_AVATAR_SERVICE_ERROR] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Ошибка получения файла");
        problem.setDetail("Не удалось получить файл");

        return ResponseEntity.status(status).body(problem);
    }

    /**
     * Обрабатывает исключение, возникающее при передаче невалидного refresh-токена.
     *
     * @param e
     *         исключение невалидного refresh-токена
     *
     * @return HTTP-ответ со статусом 401 Unauthorized и описанием ошибки
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRefreshTokenException(
            InvalidRefreshTokenException e) {

        log.error(
                "[exceptionType={}] [errorCode=INVALID_REFRESH_TOKEN] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );
        problem.setTitle(INVALID_REFRESH_TOKEN_PROBLEM_TITLE);
        problem.setProperty("errorCode", "INVALID_REFRESH_TOKEN");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleInternalServerError(Exception e) {

        log.error(
                "[exceptionType={}] [errorCode=INTERNAL_SERVER_ERROR] {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle(INTERNAL_SERVER_ERROR_PROBLEM_TITLE);
        problem.setProperty("errorCode", "INTERNAL_SERVER_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
