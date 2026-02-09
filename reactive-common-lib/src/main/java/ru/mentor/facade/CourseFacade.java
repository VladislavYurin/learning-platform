package ru.mentor.facade;

import java.util.List;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

public interface CourseFacade {

    Mono<CourseResponse> findCourseById(Long courseId);

    Mono<CourseResponse> getCourse(CourseEntity course, UserEntity user, List<ModuleEntity> modules);

    Mono<AllCoursesResponse> findAllCourses(GrpcPageRequest request);

    Mono<AllCoursesResponse> findAllActiveCourses(GrpcPageRequest request);

    Mono<List<CourseResponse>> findAllActiveCoursesPreview(GetAllActiveCoursesPreviewRequest request);

    Mono<CourseResponse> createCourse(CreateCourseGrpcRequest request, UserEntity author);

    Mono<DeleteCourseResponse> deleteCourse(Long courseId);

}
