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

@Component
@NoArgsConstructor
@AllArgsConstructor
public class CalendarServiceGrpcClient {

    @GrpcClient("calendar-service-client")
    private CalendarServiceBlockingStub blockingStub;

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

    public TimeSlotResponse bookTimeSlot(BookTimeSlotRequest bookTimeSlotRequest) {
        try {
            return blockingStub.bookTimeslot(bookTimeSlotRequest);
        } catch (Exception e) {
            throw new GrpcRetryException(String.format(
                    "[ RqUId = %s ] Ошибка отправки gRPC запроса.",
                    bookTimeSlotRequest.getRqUid()
            ), bookTimeSlotRequest.getRqUid());
        }
    }
}
