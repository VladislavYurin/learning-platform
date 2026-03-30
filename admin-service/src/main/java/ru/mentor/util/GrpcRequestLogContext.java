package ru.mentor.util;

import io.grpc.Status;
import org.slf4j.MDC;
import ru.mentor.constant.MdcKeys;

/**
 * Утилитный класс для временной установки requestId в MDC
 * на время выполнения логирующего действия в gRPC-обработчиках.
 *
 * <p>Класс нужен для того, чтобы не дублировать ручную работу с
 * {@link MDC} в каждом серверном gRPC методе и использовать requestId
 * через общий logging pattern.</p>
 */
public final class GrpcRequestLogContext {

    private static final String UNKNOWN_REQUEST_ID = "unknown";

    private GrpcRequestLogContext() {
    }

    /**
     * Выполняет действие с установленным requestId в MDC.
     *
     * @param requestId идентификатор запроса
     * @param action действие, внутри которого должен быть доступен requestId в MDC
     */
    public static void withRequestId(String requestId, Runnable action) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable(
                MdcKeys.REQUEST_ID,
                normalizeRequestId(requestId)
        )) {
            action.run();
        }
    }

    /**
     * Формирует человекочитаемое описание причины ошибки для логирования.
     *
     * @param throwable исходное исключение
     *
     * @return строка в формате {@code STATUS: description}
     */
    public static String buildErrorDescription(Throwable throwable) {
        Status status = Status.fromThrowable(throwable);
        String description = status.getDescription();

        if (description == null || description.isBlank()) {
            description = throwable.getMessage();
        }

        return status.getCode() + ": " + description;
    }

    private static String normalizeRequestId(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return UNKNOWN_REQUEST_ID;
        }
        return requestId;
    }
}
