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
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
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

    private final AdminCourseMapper courseMapper;

    private final BaseMapper baseMapper;

    /**
     * Возвращает курс по его ID и добавляет к нему инфо об авторе
     *
     * @param courseId
     *
     * @return {@link Mono<CourseResponse>}
     */
    public Mono<CourseResponse> findCourseWithAuthor(Long courseId) {
        return courseRepository
                .findByIdOrThrow(courseId)
                .flatMap(this::mapToCourseResponseWithAuthor);
    }

    /**
     * Вспомогательный метод для маппинга сущности курса в gRPC - ответ
     *
     * @param courseEntity
     */
    private Mono<CourseResponse> mapToCourseResponseWithAuthor(CourseEntity courseEntity) {
        return userRepository.findByIdOrThrow(courseEntity.getAuthorId())
                             .map(author ->
                                          courseMapper.mapCourseEntityToGrpcCourseResponse(
                                                  courseEntity,
                                                  author
                                          ));
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

        Mono<List<CourseResponse>> courseResponseMonoList =
                courseRepository.findAllBy(pageRequest)
                                .flatMap(this::toCourseResponse)
                                .collectList();

        return courseResponseMonoList
                .zipWith(
                        courseRepository.count(),
                        (courses, numberOfCourses) ->
                                new PageImpl<>(courses, pageRequest, numberOfCourses)
                )
                .map(courseMapper::mapCourseResponsePageToGrpcAllCoursesResponse);
    }

    /**
     * Маппер для преобразования сущности курса и автора в gRPC ответ
     *
     * @param courseEntity
     */
    private Mono<CourseResponse> toCourseResponse(CourseEntity courseEntity) {
        Mono<UserEntity> authorUserEntityMono =
                userRepository.findById(courseEntity.getAuthorId());
        return authorUserEntityMono
                .zipWith(
                        Mono.just(courseEntity),
                        (author, course) ->
                                courseMapper.mapCourseEntityToGrpcCourseResponse(course, author)
                );
    }

}