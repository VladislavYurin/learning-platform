package ru.mentor.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AdminCourseServiceGrpc.AdminCourseServiceBlockingStub;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.exception.GrpcRetryException;

/**
 * Клиент для обращения к сервису управления курсами через gRPC
 */
@Component
public class AdminCourseServiceGrpcClient {

    @GrpcClient("admin-course-client")
    private AdminCourseServiceBlockingStub blockingStub;

    /**
     * Возвращает запрошенный курс
     * @param request объект, содержащий данные для запроса
     *
     * @return {@link CourseResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public CourseResponse getCourse(GetCourseRequest request) {
        try {
            return blockingStub.getCourse(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getRequestId()
                    ),
                    request.getRequestId()
            );
        }
    }

    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public AllCoursesResponse getAllCourses(GrpcPageRequest request) {
        try {
            return blockingStub.getAllCourses(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getRequestId()
                    ),
                    request.getRequestId()
            );
        }
    }

}
