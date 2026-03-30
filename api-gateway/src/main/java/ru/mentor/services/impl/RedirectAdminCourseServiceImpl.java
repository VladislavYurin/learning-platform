package ru.mentor.services.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.CourseDto;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminCourseServiceGrpcClient;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.RedirectAdminCourseService;
import ru.mentor.services.UserService;

/**
 * Редирект сервис для управления курсами. Необходимы права администратора.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectAdminCourseServiceImpl implements RedirectAdminCourseService {

    private final AdminCourseServiceGrpcClient courseServiceClient;

    private final AdminCourseMapper courseMapper;

    private final BaseMapper baseMapper;

    private final HeaderFactory headerFactory;

    private final UserService userService;

    /**
     * Возвращает курс с указанным ID.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return {@link CourseDto}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CourseDto getCourseById(Long courseId) {
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Long userId = resolveUserId();
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на извлечение курса по ID. [courseId={}]",
                userId,
                courseId
        );

        GetCourseRequest getCourseRequest = courseMapper.constructGetCourseRequest(header, courseId);

        try {
            CourseResponse courseResponse = courseServiceClient.getCourse(getCourseRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от course-service на извлечение курса по ID. [courseId={}]",
                    userId,
                    courseId
            );

            return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове course-service во время извлечения курса по ID. [courseId={}]",
                    userId,
                    courseId,
                    e
            );
            throw e;
        }
    }

    /**
     * Возвращает список всех курсов.
     *
     * @param pageNumber
     *         номер страницы
     * @param pageSize
     *         размер страницы
     *
     * @return список курсов
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<CourseDto> getAllCourses(int pageNumber, int pageSize) {
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Long userId = resolveUserId();
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [pageNumber={}] [pageSize={}] Получен запрос на извлечение всех курсов.",
                userId,
                pageNumber,
                pageSize
        );

        GrpcPageRequest pageRequest = baseMapper.constructGrpcPageRequest(header, pageNumber, pageSize);

        try {
            AllCoursesResponse allCoursesResponse = courseServiceClient.getAllCourses(pageRequest);

            log.debug(
                    "[userId={}] [pageNumber={}] [pageSize={}] Успешно получен ответ от course-service на извлечение всех курсов.",
                    userId,
                    pageNumber,
                    pageSize
            );

            return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
        } catch (Exception e) {
            log.error(
                    "[userId={}] [pageNumber={}] [pageSize={}] Ошибка при вызове course-service во время извлечения всех курсов.",
                    userId,
                    pageNumber,
                    pageSize,
                    e
            );
            throw e;
        }
    }

    private Long resolveUserId() {
        return userService != null ? userService.getCurrentUserId() : null;
    }
}
