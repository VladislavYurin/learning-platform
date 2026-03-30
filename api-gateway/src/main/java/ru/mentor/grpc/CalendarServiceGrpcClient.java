package ru.mentor.grpc;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.calendar.CalendarServiceGrpc.CalendarServiceBlockingStub;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotResponse;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.MentorSlotsInfoRequest;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.TimeSlotResponse;
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
     *
     * @param request запрос на создание тайм-слота (должен содержать корректный {@code requestId})
     * @return Calendar Service с данными созданного тайм-слота
     * @throws GrpcRetryException при ошибке отправки/выполнения RPC будет перехвачен ретраем
     */
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
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в calendar-service при создании слота. cause=%s",
                            e.getMessage()
                    ), request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Бронирует слот.
     *
     * @param bookTimeSlotRequest
     *         объект, содержащий данные для бронирования
     *
     * @return {@link TimeSlotResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public TimeSlotResponse bookTimeSlot(BookTimeSlotRequest bookTimeSlotRequest) {
        try {
            return blockingStub.bookTimeslot(bookTimeSlotRequest);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в calendar-service при бронировании слота. cause=%s",
                            e.getMessage()
                    ), bookTimeSlotRequest.getHeader().getRequestId()
            );
        }
    }

    /**
     * Возвращает пустой gRPC-ответ в случае отмены слота.
     *
     * @param cancelTimeSlotRequest {@link CancelTimeSlotRequest} сгенерированный из proto класс запроса
     * @return {@link CancelTimeSlotResponse} сгенерированный класс ответа
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public CancelTimeSlotResponse cancelTimeSlot(CancelTimeSlotRequest cancelTimeSlotRequest) {
        try {
            return blockingStub.cancelTimeSlot(cancelTimeSlotRequest);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в calendar-service при отмене слота. cause=%s",
                            e.getMessage()
                    ), cancelTimeSlotRequest.getHeader().getRequestId()
            );
        }
    }

    /**
     * Возвращает gRPC-ответ с данными о слотах ментора и участниках в этих слотах
     *
     * @param mentorSlotsInfoRequest
     *         {@link MentorSlotsInfoRequest} сгенерированный из proto класс запроса
     *
     * @return {@link MentorSlotsInfoResponse} сгенерированный класс ответа
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public MentorSlotsInfoResponse getMentorSlotsInfo(MentorSlotsInfoRequest mentorSlotsInfoRequest) {
        try {
            return blockingStub.getMentorSlots(mentorSlotsInfoRequest);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в calendar-service при получении информации о слотах ментора. cause=%s",
                            e.getMessage()
                    ), mentorSlotsInfoRequest.getHeader().getRequestId()
            );
        }
    }
}
