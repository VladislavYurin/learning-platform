package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.CourseControllerApi;
import ru.mentor.gateway.model.CourseDto;
import ru.mentor.gateway.model.CreateCourseRequest;
import ru.mentor.mapper.CourseDtoMapper;
import ru.mentor.services.RedirectCourseService;

import java.util.List;

/**
 * Контроллер для управления курсами и их содержимым.
 * Предоставляет endpoints для создания, получения и управления курсами.
 */
@RestController
@RequiredArgsConstructor
public class CourseController implements CourseControllerApi {

    private final RedirectCourseService redirectCourseService;
    private final CourseDtoMapper courseDtoMapper;

    /**
     * Реализация ручки POST /course/create
     */
    @Override
    public ResponseEntity<CourseDto> createCourse(CreateCourseRequest createCourseRequest) {
        ru.mentor.dto.CourseDto commonCourseDto = redirectCourseService.createCourse(createCourseRequest);
        CourseDto apiCourseDto = courseDtoMapper.toApi(commonCourseDto);
        return ResponseEntity.ok(apiCourseDto);
    }

    /**
     * Реализация ручки DELETE /course/{courseId}
     */
    @Override
    public ResponseEntity<Object> deleteCourse(Long courseId) {
        return ResponseEntity.ok().body(redirectCourseService.getCourseById(courseId));
    }

    /**
     * Реализация ручки GET /course/all/active
     */
    @Override
    public ResponseEntity<List<CourseDto>> getAllActiveCourses() {
        List<ru.mentor.dto.CourseDto> listCommonCourseDto = redirectCourseService.getAllActiveCourses();
        List<CourseDto> listApiCourseDto = courseDtoMapper.toApiList(listCommonCourseDto);
        return ResponseEntity.ok(listApiCourseDto);
    }

    /**
     * Реализация ручки GET /course/all
     */
    @Override
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<ru.mentor.dto.CourseDto> listCommonCourseDto = redirectCourseService.getAllCourses();
        List<CourseDto> listApiCourseDto = courseDtoMapper.toApiList(listCommonCourseDto);
        return ResponseEntity.ok(listApiCourseDto);
    }

    /**
     * Реализация ручки GET /course/{courseId}
     */
    @Override
    public ResponseEntity<CourseDto> getCourseById(Long courseId) {
        ru.mentor.dto.CourseDto commonCourseDto =  redirectCourseService.getCourseById(courseId);
        CourseDto apiCourseDto = courseDtoMapper.toApi(commonCourseDto);
        return ResponseEntity.ok(apiCourseDto);
    }
}
