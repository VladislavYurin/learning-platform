package ru.mentor.grpc;

import io.grpc.Status;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.ReactorCourseServiceGrpc.CourseServiceImplBase;
import ru.mentor.error.GrpcErrorText;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.service.CourseService;

/**
 * gRPC - сервер для курсов, принимает запросы от клиента, находящегося в api-gateway.
 * Валидирует gRPC - запросы, логирует вызовы и передает их в сервис
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CourseServiceGrpcServer extends CourseServiceImplBase {

    public static final String GET_COURSE_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос на получение данных о курсе"
                    + " [ ID = {} ] от пользователя с [ ID = {} ]";
    public static final String GET_ALL_COURSES_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос  на получение данных обо всех курсах"
                    + " от пользователя с [ ID = {} ]";
    public static final String GET_ALL_ACTIVE_COURSES_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос на получение всех активных курсов"
            + " от пользователя с [ ID = {} ]";
    public static final String CREATE_COURSE_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос на создание курса [ name = {} ]"
                    + " от пользователя с [ ID = {} ]";
    public static final String DELETE_COURSE_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос на удаление курса [ ID = {} ]"
                    + " от пользователя с [ ID = {} ]";
    public static final String GET_ALL_COURSES_PREVIEW_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос  на получение данных обо всех курсах"
                    + " от пользователя с [ ID = {} ]";

    private final CourseService courseService;

    /**
     * Принимает gRPC-запросы на получение курса
     *
     * @param request - gRPC-запрос с данными курса
     *
     * @return gRPC - ответ с данными курса
     */
    @Override
    public Mono<CourseResponse> getCourse(Mono<GetCourseRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError(Status.INVALID_ARGUMENT,
                                                      GrpcErrorText.EMPTY_REQUEST))
                .doOnNext(this::logGetCourseRequest)
                .flatMap(courseService::getCourseById)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запрос на получение всех курсов
     *
     * @param request - содержит параметры пагинации
     *
     * @return - gRPC-ответ с данными курсов
     */
    @Override
    public Mono<AllCoursesResponse> getAllCourses(Mono<GrpcPageRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError(Status.INVALID_ARGUMENT,
                                                      GrpcErrorText.EMPTY_REQUEST))
                .doOnNext(this::logAllCoursesGrpcPageRequest)
                .flatMap(courseService::getAllCourses)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запрос на получение всех активных курсов
     *
     * @param request - содержит параметры пагинации
     *
     * @return - gRPC-ответ с данными курсов
     */
    @Override
    public Mono<AllCoursesResponse> getAllActiveCourses(Mono<GrpcPageRequest> request){
        return request.switchIfEmpty(toInvalidArgumentError(Status.INVALID_ARGUMENT,
                                                            GrpcErrorText.EMPTY_REQUEST))
                      .doOnNext(this::logAllActiveCoursesGrpcPageRequest)
                      .flatMap(courseService::getAllActiveCourses)
                      .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запрос на получение всех активных курсов для превью
     *
     * @param request - содержит информацию о запросе
     *
     * @return - gRPC-ответ с данными курсов без информации о модулях
     */
    public Mono<AllActiveCoursesResponse> getAllActiveCoursesPreview(
            Mono<GetAllActiveCoursesPreviewRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError(Status.INVALID_ARGUMENT,
                                                      GrpcErrorText.EMPTY_REQUEST))
                .doOnNext(this::logGetAllActiveCoursesPreviewRequest)
                .flatMap(courseService::getAllActiveCoursesPreview)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запросы на создание курса
     *
     * @param request - gRPC-запрос с данными создаваемого курса
     *
     * @return - gRPC-ответ с данными созданного курса
     */
    @Override
    public Mono<CourseResponse> createCourse(Mono<CreateCourseGrpcRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError(Status.INVALID_ARGUMENT,
                                                      GrpcErrorText.EMPTY_REQUEST))
                .doOnNext(this::logCreateCourseRequest)
                .flatMap(courseService::createCourse)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запросы на удаление курса
     *
     * @param request - gRPC-запрос с данными удаляемого курса
     *
     * @return - пустой gRPC-ответ
     */
    @Override
    public Mono<DeleteCourseResponse> deleteCourse(Mono<DeleteCourseRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError(Status.INVALID_ARGUMENT,
                                                      GrpcErrorText.EMPTY_REQUEST))
                .doOnNext(this::logDeleteCourseRequest)
                .flatMap(courseService::deleteCourse)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    private <T> Mono<T> toInvalidArgumentError(Status status, String description) {
        return Mono.error(status
                                  .withDescription(description)
                                  .asRuntimeException());
    }

    private void logGetCourseRequest(GetCourseRequest request) {
        log.info(GET_COURSE_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getCourseId(),
                 request.getSenderId());
    }

    private void logGetAllActiveCoursesPreviewRequest(GetAllActiveCoursesPreviewRequest request) {
        log.info(GET_ALL_COURSES_PREVIEW_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getSenderId());
    }

    private void logAllCoursesGrpcPageRequest(GrpcPageRequest request) {
        log.info(GET_ALL_COURSES_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getSenderId()
        );
    }

    private void logAllActiveCoursesGrpcPageRequest(GrpcPageRequest request) {
        log.info(GET_ALL_ACTIVE_COURSES_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getSenderId());
    }

    private void logCreateCourseRequest(CreateCourseGrpcRequest request) {
        log.info(CREATE_COURSE_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getCourseName(),
                 request.getUserId());
    }

    private void logDeleteCourseRequest(DeleteCourseRequest request) {
        log.info(DELETE_COURSE_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getCourseId(),
                 request.getSenderId());
    }

    private Function<EntityNotFoundException, Throwable> convertToRuntimeException() {
        return e -> Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
