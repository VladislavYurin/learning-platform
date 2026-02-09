package ru.mentor.services.impl;

import io.grpc.StatusRuntimeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseTagsGrpcClient;
import ru.mentor.mapper.CourseTagsMapper;
import ru.mentor.mapper.TagGrpcMapper;
import ru.mentor.services.RedirectCourseTagService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

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
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на создание тега для курсов юзером [ ID = {} ].",
                requestId,
                userId
        );
        CreateCourseTagGrpcRequest grpcRequest = courseTagsMapper.constructGrpcCreateRequest(
                header, userId, request
        );
        try {
            CourseTagResponse response = client.createCourseTag(grpcRequest);
            return tagGrpcMapper.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для удаления тега и передает его в gRPC-клиенту для
     * вызова сервера.
     *
     * @param tagId
     *         - ID тега, который нужно удалить
     */
    @Override
    public void deleteCourseTag(Long tagId) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на удаление тега [ ID = {} ] от юзера [ ID = {} ].",
                requestId,
                tagId,
                userId
        );
        DeleteCourseTagRequest request = courseTagsMapper.constructGrpcDeleteRequest(
                header,
                userId,
                tagId
        );
        try {
            client.deleteCourseTag(request);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения всех тегов и передает его gRPC-клиенту для передачи серверу
     *
     * @return список всех тегов
     */
    @Override
    public List<CourseTagDto> getAllTags() {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на получение всех тегов от юзера [ ID = {} ]",
                requestId, userId
        );
        GetAllCourseTagsRequest request = courseTagsMapper.constructAllCourseTagsRequest(header, userId);
        try {
            ListCourseTagsResponse response = client.getAllTags(request);
            return response.getTagsList().stream()
                           .map(tagGrpcMapper::fromGrpc)
                           .toList();
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения тега по id и передает его gRPC-клиенту для передачи серверу
     *
     * @param tagId
     *         - ID тега
     *
     * @return - ДТО с данными тега
     */
    @Override
    public CourseTagDto getTagById(Long tagId) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ RqUId = {} ] Получен запрос тега [ ID = {} ] от юзера [ ID = {} ].",
                requestId,
                tagId,
                userId
        );
        GetCourseTagRequest getCourseTagRequest = courseTagsMapper.constructGrpcGetRequest(
                header,
                userId,
                tagId
        );
        try {
            CourseTagResponse courseTagResponse = client.getCourseTag(getCourseTagRequest);
            return tagGrpcMapper.fromGrpc(courseTagResponse);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

}
