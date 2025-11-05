package ru.mentor.facade.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.facade.CourseFacade;

/**
 * Фасад для работы с курсами.
 * Абстракция для работы со связанными таблицами в реактивных репозиториях и для маппинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseFacadeImpl implements CourseFacade {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final CourseTagRepository courseTagRepository;

    private final AdminCourseMapper courseMapper;

    private final BaseMapper baseMapper;

    /**
     * Возвращает курс по его ID и добавляет к нему инфо об авторе
     *
     * @param courseId
     *
     * @return {@link Mono<CourseResponse>}
     */
    public Mono<CourseResponse> findCourseById(Long courseId) {
        return courseRepository
                .findByIdOrThrow(courseId)
                .flatMap(course ->
                                 Mono.zip(
                                         userRepository.findByIdOrThrow(course.getAuthorId()),
                                         courseTagRepository.findAllByCourseId(courseId)
                                                            .collectList(),
                                         (author, tags) ->
                                                 courseMapper.mapCourseEntityToGrpcCourseResponse(
                                                         course,
                                                         author,
                                                         tags
                                                 )
                                 )
                );
    }

    /**
     * Возвращает список всех курсов и добавляет к ним инфо об авторе, поддерживает пагинацию
     *
     * @param request
     *         - gRPC запрос курсов с параметрами пагинации
     *
     * @return - {@link Mono<AllCoursesResponse>} - сущность ответа с курсами
     */
    public Mono<AllCoursesResponse> findAllCourses(GrpcPageRequest request) {
        PageRequest pageRequest =
                baseMapper.mapGrpcPageRequestToPageRequest(request);

        Mono<List<CourseResponse>> listOfCourses =
                courseRepository
                        .findAllBy(pageRequest)
                        .flatMap(course ->
                                         Mono.zip(
                                                 userRepository.findByIdOrThrow(course.getAuthorId()),
                                                 courseTagRepository.findAllByCourseId(course.getId())
                                                                    .collectList(),
                                                 (author, tags) ->
                                                         courseMapper.mapCourseEntityToGrpcCourseResponse(
                                                                 course,
                                                                 author,
                                                                 tags
                                                         )
                                         )
                        ).collectList();

        return listOfCourses
                .zipWith(
                        courseRepository.count(),
                        (courses, numberOfCourses) ->
                                new PageImpl<>(courses, pageRequest, numberOfCourses)
                )
                .map(courseMapper::mapCourseResponsePageToGrpcAllCoursesResponse);
    }

}