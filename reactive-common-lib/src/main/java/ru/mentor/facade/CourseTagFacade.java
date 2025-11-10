package ru.mentor.facade;

import reactor.core.publisher.Mono;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagResponse;
import ru.mentor.common.ListCourseTagsResponse;

public interface CourseTagFacade {

    Mono<CourseTagResponse> createCourseTag(CreateCourseTagGrpcRequest request);

    Mono<DeleteCourseTagResponse> deleteCourseTag(Long tagId);

    Mono<CourseTagResponse> getTagById(Long tagId);

    Mono<ListCourseTagsResponse> getAllTags();

}
