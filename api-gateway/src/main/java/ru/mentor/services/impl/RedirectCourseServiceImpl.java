package ru.mentor.services.impl;

import io.grpc.StatusRuntimeException;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseServiceCourseGrpcClient;
import ru.mentor.mapper.CourseMapper;
import ru.mentor.services.RedirectCourseService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

/**
 * Реализация сервиса редиректов/интеграции для операций с курсами.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedirectCourseServiceImpl implements RedirectCourseService {

    private final UserService userService;

    private final CourseServiceCourseGrpcClient courseGrpcClient;

    private final CourseMapper courseMapper;

    private final HeaderFactory headerFactory;

    private final GrpcExceptionMapper exceptionMapper;

    /**
     * Создает gRPC-запрос для создания нового курса и передает его в gRPC-клиенту для
     * вызова сервера.
     *
     * @param request данные для создания курса
     * @return DTO созданного курса
     */
    @Override
    public CourseDto createCourse(CreateCourseRequest request) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info("[ requestId = {} ] Получен запрос на создание курса юзером [ ID = {} ].",
                requestId,
                userId
        );

        CreateCourseGrpcRequest createCourseRequest = courseMapper.toCreateCourseGrpcRequest(
                header, userId, request
        );

        try {
            CourseResponse courseResponse = courseGrpcClient.createCourse(createCourseRequest);
            return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
        } catch (StatusRuntimeException e) {
            log.error("[ requestId = {} ] Ошибка при создании курса юзером [ ID = {} ]: {}",
                    requestId, userId, e.getMessage());
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для удаления курса и передает его gRPC-клиенту.
     *
     * @param courseId идентификатор удаляемого курса
     */
    @Override
    public void deleteCourse(Long courseId) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info("[ requestId = {} ] Получен запрос на удаление курса [ ID = {} ] юзером [ ID = {} ].",
                requestId, courseId, userId);
        try {
            DeleteCourseRequest request = courseMapper.toDeleteCourseRequest(header, userId,
                    courseId);
            courseGrpcClient.deleteCourse(request);
        } catch (StatusRuntimeException e) {
            log.error("[ requestId = {} ] Ошибка при удалении курса юзером [ ID = {} ]: {}",
                    requestId, userId, e.getMessage());
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения информации о курсе и передает его gRPC-клиенту.
     * Возвращает DTO найденного курса, содержит информацию о курсе, авторе, модулях и тегах.
     *
     * @param courseId идентификатор курса
     * @return DTO найденного курса (курс, автор, модули, теги)
     */
    @Override
    public CourseDto getCourseById(Long courseId) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info("[ requestId = {} ] Получен запрос на получение курса [ ID = {} ] юзером [ ID = {} ].",
                requestId,
                courseId,
                userId);
        GetCourseRequest courseRequest = courseMapper.toGetCourseRequest(header, userId, courseId);
        try {
            CourseResponse courseResponse = courseGrpcClient.getCourse(courseRequest);
            return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
        } catch (StatusRuntimeException e) {
            log.error("[ requestId = {} ] Ошибка при получении курса юзером [ ID = {} ]: {}",
                    requestId, userId, e.getMessage());
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения всех курсов с пагинацией.
     * Возвращает список всех курсов.
     *
     * @return список курсов
     */
    @Override
    public Page<CourseDto> getAllCourses(int pageNumber, int pageSize) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info("[ requestId = {} ] Получен запрос на получение всех курсов юзером [ ID = {} ].",
                requestId,
                userId);
        GrpcPageRequest request = courseMapper.toGrpcPageRequest(header, pageNumber,
                pageSize, userId);
        try {
            AllCoursesResponse allCourses = courseGrpcClient.getAllCourses(request);
            return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCourses);
        } catch (StatusRuntimeException e) {
            log.error("[ requestId = {} ] Ошибка при получении всех курсов юзером [ ID = {} ]: {}",
                    requestId, userId, e.getMessage());
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения всех активных курсов.
     * Возвращает страницу со всеми активными курсами с пагинацией.
     *
     * @return список курсов
     */
    @Override
    public Page<CourseDto> getAllActiveCourses(int pageNumber, int pageSize) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info("[ requestId = {} ] Получен запрос на получение всех активных курсов"
                        + " юзером [ ID = {} ].",
                requestId,
                userId);
        GrpcPageRequest request = courseMapper.toGrpcPageRequest(header, pageNumber,
                pageSize, userId);
        try {
            AllCoursesResponse allActiveCourses = courseGrpcClient.getAllActiveCourses(request);
            return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allActiveCourses);
        } catch (StatusRuntimeException e) {
            log.error("[ requestId = {} ] Ошибка при получении всех активных курсов юзером [ ID = {} ]: {}",
                    requestId, userId, e.getMessage());
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Возвращает список всех активных курсов (без модулей) с информацией о наставнике
     * для превью.
     *
     * @return список DTO курсов без модулей
     */
    @Override
    public List<CourseDto> getAllActiveCoursesPreview() {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info("[ requestId = {} ] Получен запрос на получение всех активных курсов " +
                        "с информацией о наставнике от пользователя [ ID = {} ].",
                requestId,
                userId);
        GetAllActiveCoursesPreviewRequest request =
                courseMapper.toGetAllActiveCoursesPreviewRequest(header, userId);
        try {
            AllActiveCoursesResponse coursesResponse = courseGrpcClient.getAllActiveCoursesPreview(
                    request);
            return courseMapper.mapGrpcAllActiveCoursesResponseToCourseDtoList(coursesResponse);
        } catch (StatusRuntimeException e) {
            log.error("[ requestId = {} ] Ошибка при получении всех активных курсов с информацией о наставнике юзером [ ID = {} ]: {}",
                    requestId, userId, e.getMessage());
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }
}