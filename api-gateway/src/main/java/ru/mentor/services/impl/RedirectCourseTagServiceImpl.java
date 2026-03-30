package ru.mentor.services.impl;

import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseTagsGrpcClient;
import ru.mentor.mapper.CourseTagsMapper;
import ru.mentor.mapper.TagGrpcMapper;
import ru.mentor.services.RedirectCourseTagService;
import ru.mentor.services.UserService;

/**
 * Реализация сервиса редиректов/интеграции для операций с тегами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectCourseTagServiceImpl implements RedirectCourseTagService {

    private final CourseTagsGrpcClient client;
    private final TagGrpcMapper tagGrpcMapper;
    private final UserService userService;
    private final CourseTagsMapper courseTagsMapper;
    private final HeaderFactory headerFactory;
    private final GrpcExceptionMapper exceptionMapper;

    /**
     * Создает gRPC-запрос для созднания нового тега и передает его в gRPC-клиенту для
     * вызова сервера.
     *
     * @param request
     *         данные для создания курса
     *
     * @return DTO созданного курса
     */
    @Override
    public CourseTagDto createCourseTag(CreateCourseTagRequest request) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на создание тега для курсов.",
                userId
        );

        CreateCourseTagGrpcRequest grpcRequest =
                courseTagsMapper.constructGrpcCreateRequest(header, userId, request);

        try {
            CourseTagResponse response = client.createCourseTag(grpcRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-tag-service на создание тега для курсов.",
                    userId
            );

            return tagGrpcMapper.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-tag-service во время создания тега для курсов. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для удаления тега и передает его в gRPC-клиенту для
     * вызова сервера.
     *
     * @param tagId
     *         ID тега, который нужно удалить
     */
    @Override
    public void deleteCourseTag(Long tagId) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на удаление тега [tagId={}].",
                userId,
                tagId
        );

        DeleteCourseTagRequest request =
                courseTagsMapper.constructGrpcDeleteRequest(header, userId, tagId);

        try {
            client.deleteCourseTag(request);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-tag-service на удаление тега [tagId={}].",
                    userId,
                    tagId
            );
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-tag-service во время удаления тега [tagId={}]. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    tagId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения всех тегов и передает его gRPC-клиенту для передачи серверу.
     *
     * @return список всех тегов
     */
    @Override
    public List<CourseTagDto> getAllTags() {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на получение всех тегов.",
                userId
        );

        GetAllCourseTagsRequest request =
                courseTagsMapper.constructAllCourseTagsRequest(header, userId);

        try {
            ListCourseTagsResponse response = client.getAllTags(request);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-tag-service на получение всех тегов.",
                    userId
            );

            return response.getTagsList().stream()
                    .map(tagGrpcMapper::fromGrpc)
                    .toList();
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-tag-service во время получения всех тегов. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения тега по id и передает его gRPC-клиенту для передачи серверу.
     *
     * @param tagId
     *         ID тега
     *
     * @return ДТО с данными тега
     */
    @Override
    public CourseTagDto getTagById(Long tagId) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на получение тега [tagId={}].",
                userId,
                tagId
        );

        GetCourseTagRequest getCourseTagRequest =
                courseTagsMapper.constructGrpcGetRequest(header, userId, tagId);

        try {
            CourseTagResponse courseTagResponse = client.getCourseTag(getCourseTagRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-tag-service на получение тега [tagId={}].",
                    userId,
                    tagId
            );

            return tagGrpcMapper.fromGrpc(courseTagResponse);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-tag-service во время получения тега [tagId={}]. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    tagId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }
}