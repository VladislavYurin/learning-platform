package ru.mentor.exception;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

/**
 * Преобразует gRPC-исключения в кастомные runtime исключения
 */
@Component
public class GrpcExceptionMapper {

    public RuntimeException mapGrpcExceptionToRuntimeException(StatusRuntimeException e, String requestId) {
        switch (e.getStatus().getCode()) {
            case Code.NOT_FOUND -> {
                return new EntityNotFoundException(e.getStatus().getDescription(), requestId);
            }
            case Code.PERMISSION_DENIED -> {
                return new CustomAccessDeniedException(e.getStatus().getDescription(), requestId);
            }
            case Code.ALREADY_EXISTS -> {
                return new EntityAlreadyExistsException(e.getStatus().getDescription(), requestId);
            }
            default -> {
                return new GrpcRetryException("[ requestId = {} ] Ошибка отправки gRPC запроса.",
                                              requestId);
            }
        }
    }
}
