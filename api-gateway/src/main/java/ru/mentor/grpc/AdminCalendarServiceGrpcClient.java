package ru.mentor.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AdminCalendarServiceGrpc.AdminCalendarServiceBlockingStub;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.exception.GrpcRetryException;

/**
 * Клиент для обращения к сервису управления слотами через gRPC
 */
@Component
public class AdminCalendarServiceGrpcClient {

    @GrpcClient("admin-calendar-client")
    private AdminCalendarServiceBlockingStub blockingStub;

    /**
     * Возвращает все слоты в соответствии с настройками страницы
     *
     * @param pageRequest
     *         настройки страницы (номер страницы и размер)
     *
     * @return {@link AllTimeSlotsResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public AllTimeSlotsResponse getAllTimeSlots(GrpcPageRequest pageRequest) {
        try {
            return blockingStub.getAllTimeSlots(pageRequest);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            pageRequest.getHeader().getRequestId()
                    ),
                    pageRequest.getHeader().getRequestId()
            );
        }
    }

}
