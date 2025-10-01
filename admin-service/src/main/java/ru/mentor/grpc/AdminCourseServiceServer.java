package ru.mentor.grpc;

import io.grpc.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ReactorAdminCourseServiceGrpc;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.UserRepository;

/**
 * gRPC-сервис для работы с курсами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminCourseServiceServer extends
        ReactorAdminCourseServiceGrpc.AdminCourseServiceImplBase {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final AdminCourseMapper courseMapper;

    private final BaseMapper baseMapper;

    /**
     * Возвращает курс по ID
     *
     * @param request
     *         gRPC-объект {@link GetCourseRequest} запроса страницы
     */
    @Override
    public Mono<CourseResponse> getCourse(Mono<GetCourseRequest> request) {
        return request
                       .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                         .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                         .asRuntimeException()))
                       .doOnNext(monoGetCourseRequest ->
                                         log.info(
                                                 "[ rqUID = {} ] Поступил запрос  на получение данных о курсе"
                                                         + " [ ID = {} ] от администратора [ ID = {} ]",
                                                 monoGetCourseRequest.getRequestId(),
                                                 monoGetCourseRequest.getCourseId(),
                                                 monoGetCourseRequest.getAdminUserId()
                                         ))
                       .map(GetCourseRequest::getCourseId)
                       .flatMap(courseRepository::findByIdOrThrow)
                       .flatMap(courseEntity -> userRepository.findByIdOrThrow(courseEntity.getAuthorId())
                                                              .map(authorEntity -> courseMapper
                                                                                           .mapCourseEntityToGrpcCourseResponse(
                                                                                                   courseEntity,
                                                                                                   authorEntity
                                                                                           )))
                       .onErrorMap(
                               EntityNotFoundException.class, e ->
                                                                      Status.NOT_FOUND
                                                                              .withDescription(e.getMessage())
                                                                              .asRuntimeException()
                       );

    }

    /**
     * Возвращает gRPC-объект, содержащий список курсов.
     *
     * @param requestMono
     *         gRPC-объект {@link GrpcPageRequest} запроса страницы сущностей
     */
    @Override
    public Mono<AllCoursesResponse> getAllCourses(Mono<GrpcPageRequest> requestMono) {
        return requestMono
                       .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                                                         .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                                         .asRuntimeException()))
                       .doOnNext(grpcPageRequest ->
                                         log.info(
                                                 "[ rqUID = {} ] Поступил запрос  на получение данных обо всех курсах"
                                                         + " от администратора [ ID = {} ]",
                                                 grpcPageRequest.getRequestId(),
                                                 grpcPageRequest.getAdminUserId()
                                         ))
                       .flatMap(req -> {
                           PageRequest pageable = baseMapper.mapGrpcPageRequestToPageRequest(req);
                           Mono<List<CourseResponse>> courseResponseListMono =
                                   courseRepository.findAllBy(pageable)
                                                   .flatMap(this::toCourseResponse)
                                                   .collectList();
                           return courseResponseListMono
                                          .zipWith(
                                                  courseRepository.count(),
                                                  (courseList, totalCourses) -> new PageImpl<>(
                                                          courseList,
                                                          pageable,
                                                          totalCourses
                                                  )
                                          )
                                          .map(courseMapper::mapCourseResponsePageToGrpcAllCoursesResponse)
                                          .onErrorMap(
                                                  EntityNotFoundException.class,
                                                  e -> Status.NOT_FOUND
                                                               .withDescription(e.getMessage())
                                                               .asRuntimeException()
                                          );

                       });

    }

    /**
     * Формирует gRPC-ответ курса.
     *
     * @param courseEntity
     *         сущность курса
     *
     * @return Mono-объект из {@link UserEntity}
     */
    private Mono<CourseResponse> toCourseResponse(CourseEntity courseEntity) {
        Mono<UserEntity> authorUserEntityMono =
                userRepository.findById(courseEntity.getAuthorId());
        return authorUserEntityMono
                       .zipWith(
                               Mono.just(courseEntity),
                               (author, course) -> courseMapper.mapCourseEntityToGrpcCourseResponse(
                                       course,
                                       author
                               )
                       );
    }

}