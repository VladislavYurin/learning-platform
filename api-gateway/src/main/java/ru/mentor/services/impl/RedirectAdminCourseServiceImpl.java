package ru.mentor.services.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.PageSettings;
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
        log.info(
                "Получен запрос [ {} ] извлечение курса по ID [ {} ]",
                requestId, courseId
        );

        GetCourseRequest getCourseRequest = courseMapper.constructGetCourseRequest(
                requestId,
                courseId
        );

        CourseResponse courseResponse = courseServiceClient.getCourse(getCourseRequest);
        return courseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
    }

    /**
     * Возвращает страницу курсов.
     *
     * @param pageSettings
     *         настройки страницы (номер страницы и размер)
     *
     * @return объект {@link Page}, содержащий объекты {@link CourseDto}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<CourseDto> getAllCourses(PageSettings pageSettings) {

        String requestId = UUID.randomUUID().toString();
        log.info(
                "Получен запрос [ {} ] на извлечение всех курсов",
                requestId
        );

        GrpcPageRequest pageRequest = baseMapper.constructGrpcPageRequest(requestId, pageSettings);
        AllCoursesResponse allCoursesResponse = courseServiceClient.getAllCourses(pageRequest);

        return courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }

}
