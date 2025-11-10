package ru.mentor.service;

import java.util.List;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;

/**
 * Сервис для управления курсами
 * Интерфейс предоставляет методы для создания, удаления и получения информации о курсах.
 */
public interface CourseService {

    Mono<CourseResponse> createCourse(CreateCourseGrpcRequest request);

    Mono<DeleteCourseResponse> deleteCourse(DeleteCourseRequest request);

    Mono<AllCoursesResponse> getAllCourses(GrpcPageRequest request);

    Mono<CourseResponse> getCourseById(GetCourseRequest request);

    Mono<AllActiveCoursesResponse> getAllActiveCoursesPreview(GetAllActiveCoursesPreviewRequest request);
}
