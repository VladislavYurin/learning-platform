package ru.mentor.grpc;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.annotaion.GrpcMethod;
import ru.mentor.annotaion.GrpcMethodType;
import ru.mentor.calendar.CalendarServiceGrpc.CalendarServiceBlockingStub;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.exception.GrpcRetryException;

/**
 * gRPC-клиент для взаимодействия с Calendar Service.
 * Инкапсулирует вызовы блокирующего stub'а.
 */
@Component
@NoArgsConstructor
@AllArgsConstructor
public class CalendarServiceGrpcClient {

    /**
     * Блокирующий gRPC-stub Calendar Service.
     */
    @GrpcClient("calendar-service-client")
    private CalendarServiceBlockingStub blockingStub;

    /**
     * Создаёт тайм-слот наставника в Calendar Service.
     * Выполняет RPC-вызов {@code createMentorTimeSlot}.
     * @param request запрос на создание тайм-слота (должен содержать корректный {@code rqUid})
     * @return Calendar Service с данными созданного тайм-слота
     * @throws GrpcRetryException при ошибке отправки/выполнения RPC будет перехвачен ретраем
     */
    @GrpcMethod(
            grpcMethodType = GrpcMethodType.CLIENT,
            grpcInstanceType = CalendarServiceGrpcClient.class,
            requestType = CreateTimeSlotRequest.class,
            getters = {"getRqUid"}
    )
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public TimeSlotResponse createMentorTimeSlot(
            CreateTimeSlotRequest request
    ) {
        try {
            return blockingStub.createMentorTimeSlot(request);
        } catch (Exception e) {
            throw new GrpcRetryException(String.format(
                    "[ RqUId = %s ] Ошибка отправки gRPC запроса.",
                    request.getRqUid()
            ), request.getRqUid());
        }
    }

}
