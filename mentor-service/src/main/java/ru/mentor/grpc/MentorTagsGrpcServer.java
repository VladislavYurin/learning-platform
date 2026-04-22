package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagLinkEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.exception.UserException;
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
import ru.mentor.mapper.MentorTagMapper;
import ru.mentor.mentor.MentorServiceGrpc;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.impl.MentorTagServiceImpl;

import java.util.List;

/**
 * GRPC-сервис для работы с тегами менторов.
 * <p>Позволяет получать справочник тегов, доступных для менторов.
 */
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MentorTagsGrpcServer extends MentorServiceGrpc.MentorServiceImplBase {
    private final MentorTagServiceImpl mentorTagService;
    private final UserRepository userRepository;
    private final MentorTagMapper mapper;

    @Override
    public void createCustomMentorTag(
            CreateCustomMentorTagRequest request,
            StreamObserver<MentorTagResponse> responseObserver) {

        String rqUid = request.getHeader().getRequestId();
        String tagName = request.getName();
        log.info(
                "Получен запрос {} на создание кастомного тега ментора [Описание: {}].",
                rqUid,
                tagName
        );

        try {
            MentorTagDto createdMentorTag = mentorTagService.createCustomMentorTag(tagName);
            MentorTagResponse response = mapper.toMetorTagResponse(rqUid, createdMentorTag);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (Exception e) {
            responseObserver.onError(Status.ALREADY_EXISTS
                            .withDescription(e.getMessage())
                            .asRuntimeException());
        }

    }

    /**
     * Возвращает список всех тегов менторов.
     */
    @Override
    public void listMentorTags(
            AllMentorTagsRequset request,
            StreamObserver<AllMentorTagsResponse> responseObserver) {

        String rqUid = request.getHeader().getRequestId();

        log.info(
                "Получен запрос {} на получение справочника тегов менторов.",
                rqUid
        );

        try {
            List<MentorTagDto> tags = mentorTagService.getAllTags();
            AllMentorTagsResponse response = mapper.toAllMentorTagsResponse(rqUid, tags);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Успешно обработан запрос {} на получение справочника тегов менторов. Найдено тегов: {}.",
                     rqUid,
                     tags.size()
            );
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Возвращает список тегов, закреплённых за конкретным ментором.
     */
    @Override
    public void getMentorTags(GetCurrentMentorTagsRequest request, StreamObserver<MentorTagsResponse> responseObserver) {
        String rqUid = request.getHeader().getRequestId();
        long mentorId = request.getMentorId();

        log.info(
                "Получен запрос {} на теги ментора c ID = {} .",
                rqUid,
                mentorId
        );

        try {
            List<MentorTagDto> raw = mentorTagService.getMentorTags(mentorId);
            MentorTagsResponse response = mapper.toCurrentMentorTagsResponse(rqUid, raw);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserException e) {
            responseObserver.onError(Status.PERMISSION_DENIED
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }


    /**
     * Привязывает список тегов к ментору и возвращает актуальное состояние.
     */
    @Override
    public void attachMentorTags(AttachMentorTagsRequest request, StreamObserver<AttachMentorTagsResponse> responseObserver) {
        String rqUid = request.getHeader().getRequestId();
        Long mentorId = request.getMentorId();
        Set<Long> tagIds = new HashSet<>(request.getTagIdsList());

        log.info(
                "Получен запрос {} на привязку тегов к ментору [ ID = {} ]. Кол-во тегов: {}.",
                rqUid,
                mentorId,
                request.getTagIdsList().size()
        );

        try {

            List<MentorTagLinkEntity> attachedTags = mentorTagService.attachTags(
                    mentorId,
                    tagIds
            );

            List<Long> attachedTagsIds = attachedTags.stream()
                                                     .map(
                                                             attachedTag -> attachedTag.getTag().getId()
                                                     )
                                                     .toList();
            List<Long> didntAttachedIds = tagIds.stream()
                                                     .filter(id -> !attachedTagsIds.contains(id))
                                                     .toList();

            AttachMentorTagsResponse response = mapper.toAttachMentorTagsResponse(
                    rqUid,
                    attachedTagsIds,
                    didntAttachedIds
            );

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserException e) {
            responseObserver.onError(Status.PERMISSION_DENIED
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }

    /**
     * Отвязывает тег от ментора и возвращает актуальное состояние (группами).
     */
    @Override
    public void detachMentorTag(DetachMentorTagRequest request, StreamObserver<DetachMentorTagResponse> responseObserver) {
        String rqUid = request.getHeader().getRequestId();
        Long mentorId = request.getMentorId();
        Long tagId = request.getTagId();

        log.info(
                "Получен запрос {} на отвязку тега [ ID = {} ] от ментора [ ID = {} ].",
                rqUid,
                tagId,
                mentorId
        );

        try {
            mentorTagService.detachTag(mentorId, tagId);
            DetachMentorTagResponse response = mapper.toDetachMentorTagResponse(rqUid, mentorId, tagId);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserException e) {
            responseObserver.onError(Status.PERMISSION_DENIED
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }
}
