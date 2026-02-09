package ru.mentor.exception;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Преобразует gRPC-исключения в кастомные runtime исключения
 */
@Slf4j
@Component
public class GrpcExceptionMapper {

    public RuntimeException mapGrpcExceptionToRuntimeException(StatusRuntimeException e, String requestId) {

        log.info("➡️ GrpcExceptionMapper вызывается для code={}", e.getStatus().getCode());

        switch (e.getStatus().getCode()) {
            case NOT_FOUND -> {
                return new EntityNotFoundException(e.getStatus().getDescription(), requestId);
            }
            case PERMISSION_DENIED -> {
                return new CustomAccessDeniedException(e.getStatus().getDescription(), requestId);
            }
            case ALREADY_EXISTS -> {
                return new EntityAlreadyExistsException(e.getStatus().getDescription(), requestId);
            }
            case UNAVAILABLE -> {
                return new TimeSlotUnavailableException(e.getStatus().getDescription(), requestId);
            }
            default -> {
                return new GrpcRetryException("[ requestId = {} ] Ошибка отправки gRPC запроса.",
                        requestId);
            }
        }
    }
}
