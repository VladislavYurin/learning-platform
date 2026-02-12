package ru.mentor.facade.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.cache.CacheAdapter;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.facade.CourseFacade;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagLinkRepository;
import ru.mentor.repository.CourseTagRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.util.CourseAccessResolver;

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

    private final ModuleRepository moduleRepository;

    private final AdminCourseMapper courseMapper;

    private final BaseMapper baseMapper;

    private final CourseTagLinkRepository courseTagLinkRepository;

    private final CourseAccessResolver courseAccessResolver;

    private final CacheAdapter<String, List<CourseResponse>> cache;

    private static final String ACTIVE_PREVIEWS_CACHE_KEY = "active_previews";

    /**
     * Создает сущность курса из {@link CreateCourseGrpcRequest}, сущности {@link UserEntity}, и
     * сохраняет в базу данных. Инвалидирует кэш со списком превью курсов.
     *
     * @param request
     *         - gRPC запрос с данными курса
     * @param author
     *         - сущность автора, полученная в сервисном слое
     *
     * @return {@link Mono<CourseResponse>} - Mono ответ с DTO сохраненного курса
     */
    @Override
    @Transactional
    public Mono<CourseResponse> createCourse(CreateCourseGrpcRequest request, UserEntity author) {

        Mono<List<CourseTagEntity>> tags =
                request.getTagIdsList().isEmpty()
                        ? Mono.just(List.of())
                        : courseTagRepository.findAllById(request.getTagIdsList())
                                             .collectList()
                                             .flatMap(foundTagsList -> {
                                                 if (foundTagsList.size() != request.getTagIdsCount()) {
                                                     log.warn("[ requestId = {} ] Найдены не все теги,"
                                                         + " указанные в запросе на создание"
                                                         + " курса юзером [ ID = {} ]",
                                                         request.getHeader().getRequestId(),
                                                         request.getUserId()
                                                     );
                                                 }

                                                 return Mono.just(foundTagsList);
                                             });

        CourseEntity courseEntity =
                CourseEntity.builder()
                            .courseTitle(request.getCourseName())
                            .description(request.getCourseDescription())
                            .authorId(request.getUserId())
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .build();

        return tags.flatMap(tagsList ->
            courseRepository.save(courseEntity)
            .flatMap(savedCourse -> {
                List<CourseTagLinkEntity> links =
                    tagsList.stream()
                            .map(tag ->
                                CourseTagLinkEntity.builder()
                                                   .idCourse(savedCourse.getId())
                                                   .idTag(tag.getId())
                                                   .createdAt(LocalDateTime.now())
                                                   .build())
                            .toList();

                    return courseTagLinkRepository.saveAll(links)
                                                  .collectList()
                                                  .thenReturn(savedCourse)
                                                  .zipWith(Mono.just(tagsList));
            })
            .map(tuple ->
                courseMapper.mapCourseEntityToGrpcCourseResponse(tuple.getT1(),
                                                                 author,
                                                                 tuple.getT2(),
                                                                 null)
            )
            .flatMap(response -> cache.invalidate(ACTIVE_PREVIEWS_CACHE_KEY).thenReturn(response))
        );
    }

    /**
     * Находит курс по его ID, находит и добавляет к нему информацию об авторе,
     * связанные теги и модули
     *
     * @param courseId
     *         - ID курса в таблице courses
     *
     * @return {@link Mono<CourseResponse>} - gRPC ответ c DTO данными курса
     */
    public Mono<CourseResponse> findCourseById(Long courseId) {
        return courseRepository
                .findByIdOrThrow(courseId)
                .flatMap(course ->
                                 Mono.zip(
                                             userRepository.findByIdOrThrow(course.getAuthorId()),
                                             courseTagRepository.findAllByCourseId(courseId)
                                                                .collectList(),
                                             moduleRepository.findAllByCourseId(courseId)
                                                             .collectList()
                                     )
                                     .map(tuple ->
                                                  courseMapper.mapCourseEntityToGrpcCourseResponse(
                                                          course,
                                                          tuple.getT1(),
                                                          tuple.getT2(),
                                                          tuple.getT3()
                                                  )
                                     )
                );
    }

    /**
     * Находит все курсы, формирует список и добавляет к нему параметры пагинации
     *
     * @param request
     *         - gRPC запрос курсов с параметрами пагинации
     *
     * @return - {@link Mono<AllCoursesResponse>} - Mono ответ со списком DTO курсов
     */
    public Mono<AllCoursesResponse> findAllCourses(GrpcPageRequest request) {
        PageRequest pageRequest =
                baseMapper.mapGrpcPageRequestToPageRequest(request);

        return userRepository
                .findByIdOrThrow(request.getSenderId())
                .flatMap(user -> {

                    Flux<CourseEntity> courseEntityFlux = courseAccessResolver
                            .resolveCoursesForUser(user);
                    return getAllCoursesResponseMono(pageRequest, user, courseEntityFlux);
                });
    }

    private Mono<AllCoursesResponse> getAllCoursesResponseMono(
            PageRequest pageRequest,
            UserEntity user,
            Flux<CourseEntity> courseEntityFlux) {
        return courseEntityFlux
                .flatMap(course -> {
                    Flux<ModuleEntity> modulesFlux;
                    if (Role.checkIsAdmin(user) || Role.checkIsMentor(user)) {
                        modulesFlux = moduleRepository.findAllByCourseId(course.getId());
                    } else {
                        modulesFlux = moduleRepository.findAllAccessibleModules(
                                course.getId(),
                                user.getId()
                        );
                    }

                    return Mono.zip(
                            userRepository.findByIdOrThrow(course.getAuthorId()),
                            courseTagRepository.findAllByCourseId(course.getId())
                                               .collectList(),
                            modulesFlux.collectList()
                    ).map(tuple ->
                                  courseMapper.mapCourseEntityToGrpcCourseResponse(
                                          course,
                                          tuple.getT1(),
                                          tuple.getT2(),
                                          tuple.getT3()
                                  )
                    );
                })
                .collectList()
                .zipWith(
                        courseRepository.count(),
                        (courses, numberOfCourses) ->
                                new PageImpl<>(
                                        courses,
                                        pageRequest,
                                        numberOfCourses
                                )
                )
                .map(courseMapper::mapCourseResponsePageToGrpcAllCoursesResponse);
    }

    /**
     * Находит все активные курсы, формирует список и добавляет к нему параметры пагинации
     *
     * @param request
     *         - gRPC запрос курсов с параметрами пагинации
     *
     * @return - {@link Mono<AllCoursesResponse>} - Mono ответ со списком DTO курсов
     */
    @Override
    public Mono<AllCoursesResponse> findAllActiveCourses(GrpcPageRequest request) {
        PageRequest pageRequest =
                baseMapper.mapGrpcPageRequestToPageRequest(request);
        return userRepository.findByIdOrThrow(request.getSenderId())
                             .flatMap(user -> {
                                 Flux<CourseEntity> courseEntityFlux = courseAccessResolver
                                         .resolveCoursesForUser(user)
                                         .filter(CourseEntity::getIsActive);
                                 return getAllCoursesResponseMono(pageRequest, user, courseEntityFlux);
                             });
    }

    /**
     * Находит все активные курсы и возвращает их без информации о модулях. Предназначен для превью.
     * Сначала проверяет кэш, если кэш пуст - загружает из БД.
     * @param request - DTO запроса курсов
     *
     * @return - Mono со списком DTO курсов без модулей
     */
    @Override
    public Mono<List<CourseResponse>> findAllActiveCoursesPreview(GetAllActiveCoursesPreviewRequest request) {
        return cache.get(ACTIVE_PREVIEWS_CACHE_KEY, key -> loadActivePreviewsFromDb()
                .doOnNext(list -> log.info("Loaded {} active previews from DB", list.size())));
    }

    /**
     * Загружает активные курсы для превью из БД и добавляет их в кэш.
     * @return - Mono со списком DTO курсов без модулей
     */
    private Mono<List<CourseResponse>> loadActivePreviewsFromDb(){
        return courseRepository.findAllByIsActiveTrue()
                               .flatMap(course ->
                               userRepository.findByIdOrThrow(course.getAuthorId())
                                             .flatMap(author -> courseTagRepository
                                                              .findAllByCourseId(course.getId())
                                                              .collectList()
                                                              .flatMap(tags -> Mono.just(
                                                                      courseMapper.mapCourseEntityToGrpcCourseResponse(
                                                                              course, author, tags, null))
                                                              ))
                               ).collectList();
    }

    /**
     * Удаляет курс по его ID в таблице courses, а также связанные с ним записи в других таблицах БД и инвалидирует кэш.
     *
     * @param courseId - ID курса, который нужно удалить
     *
     * @return - Mono c пустым ответом
     */
    public Mono<DeleteCourseResponse> deleteCourse(Long courseId) {
        return courseRepository.deleteById(courseId)
                               .then(cache.invalidate(ACTIVE_PREVIEWS_CACHE_KEY))
                               .thenReturn(DeleteCourseResponse.newBuilder().build());
    }

    /**
     * Формирует из сущности курса, сущности пользователя, списка модулей и списка тегов
     * gRPC ответ с данными курса
     *
     * @param course сущность курса
     * @param user сущность пользователя
     * @param modules список модулей курса
     */
    @Override
    public Mono<CourseResponse> getCourse(
            CourseEntity course, UserEntity user,
            List<ModuleEntity> modules) {

        return courseTagRepository
                .findAllByCourseId(course.getId()).collectList()
                .map(tags ->
                             courseMapper.mapCourseEntityToGrpcCourseResponse(
                                     course,
                                     user,
                                     tags,
                                     modules
                             )
                );
    }

}