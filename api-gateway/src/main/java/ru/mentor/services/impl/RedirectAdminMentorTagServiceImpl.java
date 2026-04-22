package ru.mentor.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import ru.mentor.common.Header;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.mentorTag.MentorTagAttachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDetachRequestDto;
import ru.mentor.dto.mentorTag.MentorTagDetachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.dto.mentorTag.MentorTagsAttachRequestDto;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminMentorTagsGrpcClient;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.mapper.MentorTagMapper;
import ru.mentor.services.RedirectAdminMentorTagService;
import ru.mentor.services.UserService;

/**
 * Редирект сервис для управления тэгами ментора. Необходимы права администратора.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectAdminMentorTagServiceImpl implements RedirectAdminMentorTagService {
    private final AdminMentorTagsGrpcClient adminGrpcClient;
    private final MentorTagMapper mapper;
    private final HeaderFactory headerFactory;
    private final GrpcExceptionMapper exceptionMapper;
    private final UserService userService;

    @Override
    public List<MentorTagDto> allMentorTags() {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);
        Long userId = resolveUserId();

        log.debug(
                "[userId={}] Получен запрос на на получение всех тэгов менторов.",
                userId
        );

        try {
            AllMentorTagsRequset request = AllMentorTagsRequset.newBuilder()
                                                               .setHeader(header)
                                                               .build();

            AllMentorTagsResponse response = adminGrpcClient.getAllMentorTags(request);

            log.debug(
                    "[userId={}] Успешно получен ответ от mentor-service на извлечение всех тэгов.",
                    userId
            );

            return response.getAllMentorsTagsList()
                           .stream()
                           .map(mapper::mentorTagGrpcToDto)
                           .toList();
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове mentor-service во время извлечения списка всех тэгов.",
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public MentorTagDto createCustomMentorTag(MentorTagDtoCreateRequest createMentorTagRequset) {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);
        Long userId = resolveUserId();

        log.debug(
                "[userId={}] Получен запрос на создание тэга ментора.",
                userId
        );

        try {
            CreateCustomMentorTagRequest request = mapper.toCreateCustomMentorTagRequest(
                    createMentorTagRequset,
                    header
            );
            MentorTagResponse response = adminGrpcClient.createCustomMentorTag(request);
            Long newTagId = response.getMentorTag().getId();

            log.debug(
                    "[userId={}] Успешно получен ответ от mentor-service на создание нового тэга [tagId={}]",
                    userId,
                    newTagId
            );

            return mapper.responseToMentorTagDto(response);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове mentor-service во время создания нового тэга.",
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public MentorTagAttachResponseDto attachMentorTags(MentorTagsAttachRequestDto attachRequest) {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);
        Long userId = resolveUserId();

        log.debug(
                "[userId={}] Получен запрос на привязку тэгов [ ID = {} ] к ментору [ ID = {}].",
                userId,
                attachRequest.getTagsIds(),
                attachRequest.getMentorId()
        );

        try {
            AttachMentorTagsRequest request = mapper.toAttachMentorTagsRequest(
                    attachRequest,
                    header
            );
            AttachMentorTagsResponse response = adminGrpcClient.attachMentorTagsRequest(request);

            log.debug(
                    "[userId={}] Успешно получен ответ от mentor-service на привязку тэгов [tagId={}] к ментору [ID={}]",
                    userId,
                    response.getAttachedTagIdsList(),
                    attachRequest.getMentorId()
            );

            return mapper.attachResponseGrpcToDto(response);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове mentor-service во время привязки тэгов.",
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public MentorTagDetachResponseDto detachMentorTag(MentorTagDetachRequestDto detachRequest) {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);
        Long userId = resolveUserId();

        log.debug(
                "[userId={}] Получен запрос на отвязку тэга [ ID = {} ] от ментора [ ID = {}].",
                userId,
                detachRequest.getTagId(),
                detachRequest.getMentorId()
        );

        try {
            DetachMentorTagRequest request = mapper.toDetachMentorTagRequest(detachRequest, header);
            DetachMentorTagResponse response = adminGrpcClient.detachMentorTagResponse(request);

            log.debug(
                    "[userId={}] Успешно получен ответ от mentor-service на отвязку тэга [tagId={}] от ментора [ID={}]",
                    userId,
                    response.getTagId(),
                    detachRequest.getMentorId()
            );

            return mapper.detachResponseGrpcToDto(response);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове mentor-service во время отвязки тэга.",
                    userId,
                    e
            );
            throw e;
        }
    }

    private Long resolveUserId() {
        return userService != null ? userService.getCurrentUserId() : null;
    }
}
