package ru.mentor.facade;

import reactor.core.publisher.Mono;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GrpcPageRequest;

public interface CourseFacade {

    Mono<CourseResponse> findCourseWithAuthor(Long courseId);

    Mono<AllCoursesResponse> findAllCourses(GrpcPageRequest request);

}
