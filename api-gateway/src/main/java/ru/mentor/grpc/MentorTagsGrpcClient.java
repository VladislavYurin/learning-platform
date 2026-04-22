package ru.mentor.grpc;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.GetCurrentMentorTagsRequest;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.grpc.tags.MentorTagsResponse;
import ru.mentor.mentor.MentorServiceGrpc.MentorServiceBlockingStub;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MentorTagsGrpcClient {

    /**
     * Блокирующий gRPC-stub Mentor Service.
     */
    @GrpcClient("mentor-service-client")
    private MentorServiceBlockingStub blockingStub;

    /**
     * Создать кастомный тэг для ментора.
     *
     * @param CreateCustomMentorTagRequest
     *         объект, содержащий имя\описание кастомного тэга
     *
     * @return {@link MentorTagResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "{grpc.retry.delay}")
    )
    public MentorTagResponse createCustomMentorTag(
            CreateCustomMentorTagRequest request
    ) {
        try {
            return blockingStub.createCustomMentorTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в mentor-service при создании тэга. cause=%s",
                            e.getMessage()
                    ), request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Получить список всех существующих тэгов.
     *
     * @param AllMentorTagsRequset
     *         пустой запрос с хедером.
     *
     * @return {@link AllMentorTagsResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "{grpc.retry.delay}")
    )
    public AllMentorTagsResponse getAllMentorTags(
            AllMentorTagsRequset request
    ) {
        try {
            return blockingStub.listMentorTags(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в mentor-service при получении всех тэгов. cause=%s",
                            e.getMessage()
                    ), request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Получить тэги конкретного ментора.
     *
     * @param GetCurrentMentorTagsRequest
     *         объект, содержащий ID ментора.
     *
     * @return {@link MentorTagsResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "{grpc.retry.delay}")
    )
    public MentorTagsResponse getCurrentMentorTagsRequest(
            GetCurrentMentorTagsRequest request
    ) {
        try {
            return blockingStub.getMentorTags(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в mentor-service при получении тэга конкретного ментора. cause=%s",
                            e.getMessage()
                    ), request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Привязать тэги к конкретному ментору.
     *
     * @param AttachMentorTagsRequest
     *         объект, содержащий ID ментора и список ID привязываемых тэгов.
     *
     * @return {@link AttachMentorTagsResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "{grpc.retry.delay}")
    )
    public AttachMentorTagsResponse attachMentorTagsRequest(
            AttachMentorTagsRequest request
    ) {
        try {
            return blockingStub.attachMentorTags(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в mentor-service при привязке тэга. cause=%s",
                            e.getMessage()
                    ), request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отвязать тэг от конкретного ментора.
     *
     * @param DetachMentorTagRequest
     *         объект, содержащий ID ментора и ID тэга.
     *
     * @return {@link DetachMentorTagResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "{grpc.retry.delay}")
    )
    public DetachMentorTagResponse detachMentorTagResponse(
            DetachMentorTagRequest request
    ) {
        try {
            return blockingStub.detachMentorTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в mentor-service при отвязке тэга. cause=%s",
                            e.getMessage()
                    ), request.getHeader().getRequestId()
            );
        }
    }
}
