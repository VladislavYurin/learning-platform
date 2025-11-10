package ru.mentor.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CourseServiceGrpc.CourseServiceBlockingStub;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.exception.GrpcRetryException;

/**
 * gRPC - клиент для отправки запросов, связанных с курсами в course-service
 */
@Component
@Retryable(
        retryFor = GrpcRetryException.class,
        maxAttemptsExpression = "${grpc.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
)
public class CourseServiceCourseGrpcClient {

    @GrpcClient("course-client")
    private CourseServiceBlockingStub courseServiceBlockingStub;

    /**
     * Отправляет в course-service gRPC-запрос для создания нового курса
     *
     * @param request
     *         - gRPC-запрос с данными создаваемого курса
     *
     * @return - gRPC-ответ с данными созданного курса
     */
    public CourseResponse createCourse(CreateCourseGrpcRequest request) {
        try {
            return courseServiceBlockingStub.createCourse(request);
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
     * Отправляет в course-service gRPC-запрос для получение данных курса
     *
     * @param request
     *         - gRPC-запрос курса
     *
     * @return - gRPC-ответ с данными курса
     */
    public CourseResponse getCourse(GetCourseRequest request) {
        try {
            return courseServiceBlockingStub.getCourse(request);
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
     * Отправляет в course-service gRPC-запрос для удаления курса
     *
     * @param request
     *         - gRPC-запрос с данными для удаления курса
     *
     * @return - пустой gRPC-ответ
     */
    public DeleteCourseResponse deleteCourse(DeleteCourseRequest request) {
        try {
            return courseServiceBlockingStub.deleteCourse(request);
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
     * Отправляет в course-service gRPC-запрос для получения всех курсов
     *
     * @param request
     *         - gRPC-запрос с данными для получения курсов
     *
     * @return - gRPC-ответ с курсами
     */
    public AllCoursesResponse getAllCourses(GrpcPageRequest request) {
        try {
            return courseServiceBlockingStub.getAllCourses(request);
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

    public AllCoursesResponse getAllActiveCourses(GrpcPageRequest request) {
        try {
            return courseServiceBlockingStub.getAllActiveCourses(request);
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

    public AllActiveCoursesResponse getAllActiveCoursesPreview(GetAllActiveCoursesPreviewRequest request) {
        try {
            return courseServiceBlockingStub.getAllActiveCoursesPreview(request);
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

}
