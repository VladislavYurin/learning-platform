package ru.mentor.grpc;

import io.grpc.Status;
import java.util.function.Function;
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
import ru.mentor.util.GrpcRequestLogContext;

/**
 * gRPC-сервис для работы с курсами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminCourseServiceServer extends
        ReactorAdminCourseServiceGrpc.AdminCourseServiceImplBase {

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
                .flatMap(getCourseRequest -> {
                    String requestId = getCourseRequest.getHeader().getRequestId();

                    GrpcRequestLogContext.withRequestId(requestId, () ->
                            log.debug(
                                    "Получен gRPC запрос на получение курса: [courseId={}] [senderId={}]",
                                    getCourseRequest.getCourseId(),
                                    getCourseRequest.getSenderId()
                            )
                    );

                    return courseFacade.findCourseById(getCourseRequest.getCourseId())
                            .doOnSuccess(response ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.debug(
                                                    "Успешно обработан gRPC запрос на получение курса: [courseId={}] [senderId={}]",
                                                    getCourseRequest.getCourseId(),
                                                    getCourseRequest.getSenderId()
                                            )
                                    )
                            )
                            .doOnError(error ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.error(
                                                    "Ошибка обработки gRPC запроса на получение курса: [courseId={}] [senderId={}] [cause={}]",
                                                    getCourseRequest.getCourseId(),
                                                    getCourseRequest.getSenderId(),
                                                    GrpcRequestLogContext.buildErrorDescription(error),
                                                    error
                                            )
                                    )
                            );
                })
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
                .flatMap(grpcPageRequest -> {
                    String requestId = grpcPageRequest.getHeader().getRequestId();

                    GrpcRequestLogContext.withRequestId(requestId, () ->
                            log.debug(
                                    "Получен gRPC запрос на получение страницы курсов: [pageNumber={}] [pageSize={}] [senderId={}]",
                                    grpcPageRequest.getPageNumber(),
                                    grpcPageRequest.getPageSize(),
                                    grpcPageRequest.getSenderId()
                            )
                    );

                    return courseFacade.findAllCourses(grpcPageRequest)
                            .doOnSuccess(response ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.debug(
                                                    "Успешно обработан gRPC запрос на получение страницы курсов: [pageNumber={}] [pageSize={}] [senderId={}]",
                                                    grpcPageRequest.getPageNumber(),
                                                    grpcPageRequest.getPageSize(),
                                                    grpcPageRequest.getSenderId()
                                            )
                                    )
                            )
                            .doOnError(error ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.error(
                                                    "Ошибка обработки gRPC запроса на получение страницы курсов: [pageNumber={}] [pageSize={}] [senderId={}] [cause={}]",
                                                    grpcPageRequest.getPageNumber(),
                                                    grpcPageRequest.getPageSize(),
                                                    grpcPageRequest.getSenderId(),
                                                    GrpcRequestLogContext.buildErrorDescription(error),
                                                    error
                                            )
                                    )
                            );
                })
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

    private Mono<? extends GetCourseRequest> toInvalidArgumentError() {
        return Mono.error(Status.INVALID_ARGUMENT
                .withDescription(GrpcErrorText.EMPTY_REQUEST)
                .asRuntimeException());
    }
}