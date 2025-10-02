package ru.mentor.grpc;

import io.grpc.Status;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.admin.ReactorAdminMentorServiceGrpc;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.exception.UserException;
import ru.mentor.facade.MentorTagFacade;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.mapper.MentorTagMapperReactive;

/**
 * gRPC-сервис для работы с тэгами ментора для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminMentorServiceServer extends
        ReactorAdminMentorServiceGrpc.AdminMentorServiceImplBase {

    private final MentorTagFacade mentorTagFacade;
    private final MentorTagMapperReactive mentorTagMapperReactive;

    /**
     * Возвращает созданный тэг.
     */
    @Override
    public Mono<MentorTagResponse> createCustomMentorTag(Mono<CreateCustomMentorTagRequest> request) {

        return request
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                  .asRuntimeException()))
                .doOnNext(grpcRequest ->
                                  log.info(
                                          "[requestId={}] Поступил запрос на создание кастомного тэга ментора.",
                                          grpcRequest.getHeader().getRequestId()
                                  ))
                .flatMap(grpcRequest ->
                                 mentorTagFacade.createCustomMentorTag(grpcRequest)
                                                .map(createdTag ->
                                                             mentorTagMapperReactive.toMetorTagResponse(
                                                                     grpcRequest.getHeader()
                                                                                .getRequestId(),
                                                                     createdTag
                                                             )
                                                )
                )
                .onErrorMap(
                        EntityAlreadyExistsException.class,
                        e -> Status.ALREADY_EXISTS
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );
    }

    /**
     * Возвращает список всех тегов менторов.
     */
    @Override
    public Mono<AllMentorTagsResponse> listMentorTags(Mono<AllMentorTagsRequset> request) {

        return request
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                  .asRuntimeException()))
                .doOnNext(grpcRequest ->
                                  log.info(
                                          "[requestId={}] Поступил запрос на получение справочника тегов менторов.",
                                          grpcRequest.getHeader().getRequestId()
                                  ))
                .flatMap(grpcRequest ->
                                 mentorTagFacade.allMentorTags(grpcRequest)
                                                .map(tagsList ->
                                                             mentorTagMapperReactive.toAllMentorTagsResponse(
                                                                     grpcRequest.getHeader()
                                                                                .getRequestId(),
                                                                     tagsList
                                                             )
                                                )
                )
                .onErrorMap(
                        EntityNotFoundException.class,
                        e -> Status.NOT_FOUND
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );
    }

    /**
     * Привязывает список тегов к ментору и возвращает актуальное состояние.
     */
    @Override
    public Mono<AttachMentorTagsResponse> attachMentorTags(
            Mono<AttachMentorTagsRequest> request) {

        return request
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                  .asRuntimeException()))
                .doOnNext(grpcRequest ->
                                  log.info(
                                          "[requestId={}] Поступил запрос на привязку тегов к ментору [ ID = {} ]. Кол-во тегов: {}.",
                                          grpcRequest.getHeader().getRequestId(),
                                          grpcRequest.getMentorId(),
                                          grpcRequest.getTagIdsList().size()
                                  ))
                .flatMap(grpcRequest ->
                                 mentorTagFacade.attachMentorTags(grpcRequest)
                                                .map(attachedIds -> {
                                                    Set<Long> uniqAttachRequestIds = new HashSet<>(
                                                            grpcRequest.getTagIdsList());

                                                    return mentorTagMapperReactive.toAttachMentorTagsResponse(
                                                            grpcRequest.getHeader().getRequestId(),
                                                            attachedIds,
                                                            uniqAttachRequestIds.stream()
                                                                                .filter(id -> !attachedIds.contains(
                                                                                        id))
                                                                                .toList()
                                                    );
                                                })
                )
                .onErrorMap(
                        EntityNotFoundException.class,
                        e -> Status.NOT_FOUND
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                )
                .onErrorMap(
                        UserException.class,
                        e -> Status.INVALID_ARGUMENT
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );

    }

    /**
     * Отвязывает тег от ментора и возвращает актуальное состояние (группами).
     */
    @Override
    public Mono<DetachMentorTagResponse> detachMentorTag(
            Mono<DetachMentorTagRequest> request) {
        return request
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                  .asRuntimeException()))
                .doOnNext(grpcRequest ->
                                  log.info(
                                          "[requestId={}] Поступил запрос на отвязку тега [ ID = {} ] от ментора [ ID = {} ].",
                                          grpcRequest.getHeader().getRequestId(),
                                          grpcRequest.getTagId(),
                                          grpcRequest.getMentorId()
                                  ))
                .flatMap(grpcRequest ->
                                 mentorTagFacade.detachMentorTag(grpcRequest)
                                                .then(Mono.fromSupplier(() ->
                                                                                mentorTagMapperReactive.toDetachMentorTagResponse(
                                                                                        grpcRequest.getHeader()
                                                                                                   .getRequestId(),
                                                                                        grpcRequest.getMentorId(),
                                                                                        grpcRequest.getTagId()
                                                                                )

                                                ))
                )
                .onErrorMap(
                        EntityNotFoundException.class,
                        e -> Status.NOT_FOUND
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );
    }

}
