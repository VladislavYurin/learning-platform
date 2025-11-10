package ru.mentor.service;

import reactor.core.publisher.Mono;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.DeleteCourseTagResponse;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.ListCourseTagsResponse;

/**
 * Сервис для создания, удаления и получения тегов курсов
 */
public interface CourseTagService {

    Mono<CourseTagResponse> createCourseTag(CreateCourseTagGrpcRequest request);

    Mono<DeleteCourseTagResponse> deleteCourseTag(DeleteCourseTagRequest request);

    Mono<CourseTagResponse> getTagById(GetCourseTagRequest request);

    Mono<ListCourseTagsResponse> getAllTags();
}
