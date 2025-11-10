package ru.mentor.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CourseTagsServiceGrpc;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.DeleteCourseTagResponse;
import ru.mentor.common.Empty;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.exception.GrpcRetryException;

/**
 * gRPC-клиент, отправляет запросы в course-service для работы с тегами курсов
 */
@Component
@Retryable(
        retryFor = GrpcRetryException.class,
        maxAttemptsExpression = "${grpc.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
)
public class CourseTagsGrpcClient {

    @GrpcClient("course-tag-client")
    private CourseTagsServiceGrpc.CourseTagsServiceBlockingStub stub;

    /**
     * Отправляет gRPC-запрос для создания нового тега
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return - ДТО созданного тега
     */
    public CourseTagResponse createCourseTag(CreateCourseTagGrpcRequest request) {
        try {
            return stub.createCourseTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для удаления тега
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return - пустой gRPC-ответ
     */
    public DeleteCourseTagResponse deleteCourseTag(DeleteCourseTagRequest request) {
        try {
            return stub.deleteCourseTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для получения тега
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return - ДТО тега
     */
    public CourseTagResponse getCourseTag(GetCourseTagRequest request) {
        try {
            return stub.getCourseTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для получения всех существующих тегов
     *
     * @return - список ДТО тегов
     */
    public ListCourseTagsResponse getAllTags() {
        try {
            return stub.getAllTags(Empty.getDefaultInstance());
        } catch (Exception e) {
            throw new GrpcRetryException("Ошибка отправки gRPC запроса.");
        }
    }

}
