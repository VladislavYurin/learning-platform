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
import ru.mentor.common.GetAllCourseTagsRequest;
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

    public CourseTagResponse createCourseTag(CreateCourseTagGrpcRequest request) {
        try {
            return stub.createCourseTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в course-tag-service при создании тега. cause=%s",
                            e.getMessage()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    public DeleteCourseTagResponse deleteCourseTag(DeleteCourseTagRequest request) {
        try {
            return stub.deleteCourseTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в course-tag-service при удалении тега. cause=%s",
                            e.getMessage()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    public CourseTagResponse getCourseTag(GetCourseTagRequest request) {
        try {
            return stub.getCourseTag(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в course-tag-service при получении тега. cause=%s",
                            e.getMessage()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    public ListCourseTagsResponse getAllTags(GetAllCourseTagsRequest request) {
        try {
            return stub.getAllTags(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "Ошибка отправки gRPC запроса в course-tag-service при получении всех тегов. cause=%s",
                            e.getMessage()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }
}
