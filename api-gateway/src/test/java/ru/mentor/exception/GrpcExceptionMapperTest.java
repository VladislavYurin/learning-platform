package ru.mentor.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GrpcExceptionMapperTest {

    private static final String REQUEST_ID = "rq-123";
    private final GrpcExceptionMapper grpcExceptionMapper = new GrpcExceptionMapper();

    @Test
    void mapGrpcExceptionToRuntimeException_notFound_returnsEntityNotFoundException() {
        String errorMessage = "Сущность не найдена";
        StatusRuntimeException grpcException =
                Status.NOT_FOUND.withDescription(errorMessage).asRuntimeException();

        RuntimeException result = grpcExceptionMapper
                .mapGrpcExceptionToRuntimeException(grpcException, REQUEST_ID);

        EntityNotFoundException exception = assertInstanceOf(EntityNotFoundException.class, result);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(REQUEST_ID, exception.getRequestId());
    }

    @Test
    void mapGrpcExceptionToRuntimeException_permissionDenied_returnsCustomAccessDeniedException() {
        String errorMessage = "Доступ запрещен";
        StatusRuntimeException grpcException =
                Status.PERMISSION_DENIED.withDescription(errorMessage).asRuntimeException();

        RuntimeException result = grpcExceptionMapper
                .mapGrpcExceptionToRuntimeException(grpcException, REQUEST_ID);

        CustomAccessDeniedException exception = assertInstanceOf(CustomAccessDeniedException.class, result);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(REQUEST_ID, exception.getRequestId());
    }

    @Test
    void mapGrpcExceptionToRuntimeException_alreadyExists_returnsEntityAlreadyExistsException() {
        String errorMessage = "Сущность уже существует";
        StatusRuntimeException grpcException =
                Status.ALREADY_EXISTS.withDescription(errorMessage).asRuntimeException();

        RuntimeException result = grpcExceptionMapper
                .mapGrpcExceptionToRuntimeException(grpcException, REQUEST_ID);

        EntityAlreadyExistsException exception = assertInstanceOf(EntityAlreadyExistsException.class, result);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(REQUEST_ID, exception.getRequestId());
    }

    @Test
    void mapGrpcExceptionToRuntimeException_otherStatus_returnsGrpcRetryException() {
        StatusRuntimeException grpcException =
                Status.INTERNAL.withDescription("Внутренняя ошибка").asRuntimeException();

        RuntimeException result = grpcExceptionMapper
                .mapGrpcExceptionToRuntimeException(grpcException, REQUEST_ID);

        GrpcRetryException exception = assertInstanceOf(GrpcRetryException.class, result);
        assertEquals(
                "Ошибка при вызове внутреннего gRPC-сервиса. grpcStatusCode=INTERNAL, grpcDescription=Внутренняя ошибка",
                exception.getMessage());
        assertEquals(REQUEST_ID, exception.getRequestId());
    }
}
