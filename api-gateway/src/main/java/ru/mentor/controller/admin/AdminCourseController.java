package ru.mentor.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.model.CourseDto;
import ru.mentor.gateway.api.AdminCourseControllerApi;
import ru.mentor.gateway.model.PageCourseDto;
import ru.mentor.mapper.PageCourseDtoMapper;
import ru.mentor.services.RedirectAdminCourseService;

/**
 * Контроллер управления курсами для администратора.
 */
@RestController
@RequiredArgsConstructor
class AdminCourseController implements AdminCourseControllerApi {

    private final RedirectAdminCourseService redirectAdminCourseService;
    private final PageCourseDtoMapper pageCourseDtoMapper;

    /**
     * Реализация ручки GET /admin/course/{courseId}
     */
    @Override
    public ResponseEntity<CourseDto> adminGetCourseById(Long courseId) {
        CourseDto course = redirectAdminCourseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    /**
     * Реализация ручки GET /admin/course/all
     */
    @Override
    public ResponseEntity<PageCourseDto> adminGetAllCourses(Integer pageNumber, Integer pageSize) {
        Page<CourseDto> page = redirectAdminCourseService.getAllCourses(pageNumber, pageSize);
        return ResponseEntity.ok(pageCourseDtoMapper.toDto(page));
    }

}
