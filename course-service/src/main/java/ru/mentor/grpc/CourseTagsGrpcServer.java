package ru.mentor.grpc;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.DeleteCourseTagResponse;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.common.ReactorCourseTagsServiceGrpc;
import ru.mentor.error.GrpcErrorText;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.service.CourseTagService;

/**
 * gRPC-сервер для работы с тегами.
 *
 * <p>Текущая реализация поддерживает получение списка всех тегов.</p>
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CourseTagsGrpcServer extends ReactorCourseTagsServiceGrpc.CourseTagsServiceImplBase {

    private final CourseTagService courseTagService;

    public static final String CREATE_COURSE_TAG_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на создание тега [ name = {} ]"
                    + " от пользователя с [ ID = {} ]";
    public static final String GET_COURSE_TAG_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на получение тега"
                    + " [ ID = {} ] от пользователя с [ ID = {} ]";
    public static final String DELETE_COURSE_TAG_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на удаление тега [ ID = {} ]"
                    + " от пользователя с [ ID = {} ]";

    @Override
    public Mono<CourseTagResponse> createCourseTag(Mono<CreateCourseTagGrpcRequest> request){
        return request.switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                        .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                        .asRuntimeException()))
                .doOnNext(grpcRequest->log.info(CREATE_COURSE_TAG_REQUEST_LOG_TEXT,
                                                 grpcRequest.getHeader().getRequestId(),
                                                 grpcRequest.getSenderId(),
                                                 grpcRequest.getName()))
                .flatMap(courseTagService::createCourseTag)
                .onErrorMap(EntityNotFoundException.class, e -> Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .asRuntimeException());
    }

    @Override
    public Mono<DeleteCourseTagResponse> deleteCourseTag(Mono<DeleteCourseTagRequest> request) {
        return request.switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                        .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                        .asRuntimeException()))
                .doOnNext(grpcRequest->log.info(DELETE_COURSE_TAG_REQUEST_LOG_TEXT,
                                                grpcRequest.getHeader().getRequestId(),
                                                grpcRequest.getSenderId(),
                                                grpcRequest.getTagId()))
                .flatMap(courseTagService::deleteCourseTag)
                .onErrorMap(EntityNotFoundException.class, e -> Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .asRuntimeException());
    }

    @Override
    public Mono<CourseTagResponse> getCourseTag(Mono<GetCourseTagRequest> request) {
        return request.switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                        .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                        .asRuntimeException()))
                .doOnNext(grpcRequest->log.info(GET_COURSE_TAG_REQUEST_LOG_TEXT,
                                                grpcRequest.getHeader().getRequestId(),
                                                grpcRequest.getSenderId(),
                                                grpcRequest.getTagId()))
                .flatMap(courseTagService::getTagById)
                .onErrorMap(EntityNotFoundException.class, e -> Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .asRuntimeException());
    }

    @Override
    public Mono<ListCourseTagsResponse> getAllTags(Mono<GetAllCourseTagsRequest> request) {
        return request
                .flatMap(empty -> courseTagService.getAllTags())
                .onErrorMap(EntityNotFoundException.class, e -> Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .asRuntimeException());
    }
}
