package ru.mentor.services.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminCourseServiceGrpcClient;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.RedirectAdminCourseService;

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

        String requestId = UUID.randomUUID().toString();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос извлечение курса по ID [ {} ]",
                requestId, courseId
        );

        GetCourseRequest getCourseRequest = courseMapper.constructGetCourseRequest(
                header,
                courseId
        );

        CourseResponse courseResponse = courseServiceClient.getCourse(getCourseRequest);
        return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
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

        String requestId = UUID.randomUUID().toString();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на извлечение всех курсов",
                requestId
        );

        GrpcPageRequest pageRequest = baseMapper.constructGrpcPageRequest(
                header,
                pageNumber,
                pageSize
        );
        AllCoursesResponse allCoursesResponse = courseServiceClient.getAllCourses(pageRequest);

        return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }

}
