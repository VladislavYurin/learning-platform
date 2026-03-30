package ru.mentor.services.impl;

import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseServiceCourseGrpcClient;
import ru.mentor.mapper.CourseMapper;
import ru.mentor.services.RedirectCourseService;
import ru.mentor.services.UserService;

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
     * @param request
     *         данные для создания курса
     *
     * @return DTO созданного курса
     */
    @Override
    public CourseDto createCourse(CreateCourseRequest request) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на создание курса.",
                userId
        );

        CreateCourseGrpcRequest createCourseRequest =
                courseMapper.constructGrpcCreateRequest(header, userId, request);

        try {
            CourseResponse courseResponse = courseGrpcClient.createCourse(createCourseRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-service на создание курса.",
                    userId
            );

            return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-service во время создания курса. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для удаления курса и передает его gRPC-клиенту.
     *
     * @param courseId
     *         идентификатор удаляемого курса
     */
    @Override
    public void deleteCourse(Long courseId) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [courseId={}] Получен запрос на удаление курса.",
                userId,
                courseId
        );

        try {
            DeleteCourseRequest request =
                    courseMapper.constructGrpcDeleteRequest(header, userId, courseId);
            courseGrpcClient.deleteCourse(request);

            log.debug(
                    "[userId={}] [courseId={}] Успешно получен ответ от course-service на удаление курса.",
                    userId,
                    courseId
            );
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] [courseId={}] Ошибка при вызове course-service во время удаления курса. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    courseId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для получения информации о курсе и передает его gRPC-клиенту.
     * Возвращает DTO найденного курса, содержит информацию о курсе, авторе, модулях и тегах.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return DTO найденного курса (курс, автор, модули, теги)
     */
    @Override
    public CourseDto getCourseById(Long courseId) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [courseId={}] Получен запрос на получение курса.",
                userId,
                courseId
        );

        GetCourseRequest courseRequest =
                courseMapper.constructGrpcGetRequest(header, userId, courseId);

        try {
            CourseResponse courseResponse = courseGrpcClient.getCourse(courseRequest);

            log.debug(
                    "[userId={}] [courseId={}] Успешно получен ответ от course-service на получение курса.",
                    userId,
                    courseId
            );

            return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] [courseId={}] Ошибка при вызове course-service во время получения курса. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    courseId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
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
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [pageNumber={}] [pageSize={}] Получен запрос на получение всех курсов.",
                userId,
                pageNumber,
                pageSize
        );

        GrpcPageRequest request =
                courseMapper.constructGrpcPageRequest(header, pageNumber, pageSize, userId);

        try {
            AllCoursesResponse allCourses = courseGrpcClient.getAllCourses(request);

            log.debug(
                    "[userId={}] [pageNumber={}] [pageSize={}] Успешно получен ответ от course-service на получение всех курсов.",
                    userId,
                    pageNumber,
                    pageSize
            );

            return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCourses);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] [pageNumber={}] [pageSize={}] Ошибка при вызове course-service во время получения всех курсов. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    pageNumber,
                    pageSize,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
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
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [pageNumber={}] [pageSize={}] Получен запрос на получение всех активных курсов.",
                userId,
                pageNumber,
                pageSize
        );

        GrpcPageRequest request =
                courseMapper.constructGrpcPageRequest(header, pageNumber, pageSize, userId);

        try {
            AllCoursesResponse allActiveCourses = courseGrpcClient.getAllActiveCourses(request);

            log.debug(
                    "[userId={}] [pageNumber={}] [pageSize={}] Успешно получен ответ от course-service на получение всех активных курсов.",
                    userId,
                    pageNumber,
                    pageSize
            );

            return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allActiveCourses);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] [pageNumber={}] [pageSize={}] Ошибка при вызове course-service во время получения всех активных курсов. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    pageNumber,
                    pageSize,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
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
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на получение всех активных курсов с информацией о наставнике.",
                userId
        );

        GetAllActiveCoursesPreviewRequest request =
                courseMapper.constructGetAllActiveCoursesPreviewRequest(header, userId);

        try {
            AllActiveCoursesResponse coursesResponse =
                    courseGrpcClient.getAllActiveCoursesPreview(request);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-service на получение всех активных курсов с информацией о наставнике.",
                    userId
            );

            return courseMapper.mapGrpcAllActiveCoursesResponseToCourseDtoList(coursesResponse);
        } catch (StatusRuntimeException e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-service во время получения всех активных курсов с информацией о наставнике. [grpcStatusCode={}] [grpcDescription={}]",
                    userId,
                    e.getStatus().getCode(),
                    e.getStatus().getDescription(),
                    e
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }
}