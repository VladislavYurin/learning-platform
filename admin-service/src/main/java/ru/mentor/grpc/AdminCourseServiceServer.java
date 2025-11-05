package ru.mentor.grpc;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.admin.ReactorAdminCourseServiceGrpc;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.error.GrpcErrorText;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.facade.CourseFacade;
import java.util.function.Function;

/**
 * gRPC-сервис для работы с курсами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminCourseServiceServer extends
        ReactorAdminCourseServiceGrpc.AdminCourseServiceImplBase {

    public static final String GET_COURSE_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на получение данных о курсе"
                    + " [ ID = {} ] от администратора [ ID = {} ]";
    public static final String GET_ALL_COURSES_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос  на получение данных обо всех курсах"
                    + " от администратора [ ID = {} ]";


    private final CourseFacade courseFacade;

    /**
     * Возвращает курс по ID
     *
     * @param request
     *         gRPC-объект {@link GetCourseRequest} запроса страницы
     */
    @Override
    public Mono<CourseResponse> getCourse(Mono<GetCourseRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError())
                .doOnNext(this::logGetCourseRequest)
                .map(GetCourseRequest::getCourseId)
                .flatMap(courseFacade::findCourseById)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Возвращает gRPC-объект, содержащий список курсов.
     *
     * @param requestMono
     *         gRPC-объект {@link GrpcPageRequest} запроса страницы сущностей
     */
    @Override
    public Mono<AllCoursesResponse> getAllCourses(Mono<GrpcPageRequest> requestMono) {

        return requestMono
                .switchIfEmpty(emptyGrpcPageRequestCheck())
                .doOnNext(this::logAllCoursesGrpcPageRequest)
                .flatMap(courseFacade::findAllCourses)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    private Function<EntityNotFoundException, Throwable> convertToRuntimeException() {
        return e -> Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    private Mono<? extends GrpcPageRequest> emptyGrpcPageRequestCheck() {
        return Mono.error(Status.INVALID_ARGUMENT
                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                  .asRuntimeException());
    }

    private void logAllCoursesGrpcPageRequest(GrpcPageRequest request) {
        log.info(GET_ALL_COURSES_REQUEST_LOG_TEXT,
                request.getHeader().getRequestId(),
                request.getSenderId()
        );
    }

    private void logGetCourseRequest(GetCourseRequest request) {
        log.info(GET_COURSE_REQUEST_LOG_TEXT,
                request.getHeader().getRequestId(),
                request.getCourseId(),
                request.getSenderId()
        );
    }

    private Mono<? extends GetCourseRequest> toInvalidArgumentError() {
        return Mono.error(Status.INVALID_ARGUMENT
                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                  .asRuntimeException());

    }

}