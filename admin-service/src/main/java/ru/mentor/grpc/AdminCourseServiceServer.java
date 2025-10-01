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
                       .doOnNext(monoGetCourseRequest -> {
                           log.info(
                                   "[ rqUID = {} ] Поступил запрос  на получение данных о курсе"
                                           + " [ ID = {} ] от администратора",
                                   monoGetCourseRequest.getRequestId(),
                                   monoGetCourseRequest.getCourseId()
                           );
                       })
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
                       .doOnNext(req -> log.info(
                               "[RqUid={}] Поступил запрос на получение списка всех курсов, pageNumber={}, pageSize={}",
                               req.getRequestId(),
                               req.getPageNumber(),
                               req.getPageSize()
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

//    @Override
//    public void getCourse(
//            GetCourseRequest request,
//            StreamObserver<CourseResponse> responseObserver) {
//
//        String requestId = request.getRequestId();
//        long courseId = request.getCourseId();
//        log.info(
//                "[ rqUID = {} ] Поступил запрос  на получение данных о курсе [ ID = {} ] от администратора",
//                requestId,
//                courseId
//        );
//
//        try {
//            CourseEntity courseEntity = courseRepository.findByIdOrThrow(courseId);
//            CourseResponse courseResponse = courseMapper
//                                                    .mapCourseEntityToGrpcCourseResponse(
//                                                            courseEntity);
//
//            responseObserver.onNext(courseResponse);
//            responseObserver.onCompleted();
//
//        } catch (EntityNotFoundException e) {
//            responseObserver.onError(Status.NOT_FOUND
//                                             .withDescription(e.getMessage())
//                                             .asRuntimeException());
//        }
//
//    }
//
//    @Override
//    public void getAllCourses(
//            GrpcPageRequest request,
//            StreamObserver<AllCoursesResponse> responseObserver) {
//
//        String requestId = request.getRequestId();
//        log.info(
//                "[ rqUID = {} ] Поступил запрос на получение данных обо всех курсах от администратора",
//                requestId
//        );
//
//        try {
//            PageRequest pageRequest = baseMapper.mapGrpcPageRequestToPageRequest(request);
//            Page<CourseEntity> courseEntity = courseRepository.findAll(pageRequest);
//
//            AllCoursesResponse allCoursesResponse = courseMapper
//                                                            .mapCourseEntityPageToGrpcAllCoursesResponse(
//                                                                    courseEntity);
//
//            responseObserver.onNext(allCoursesResponse);
//            responseObserver.onCompleted();
//
//        } catch (EntityNotFoundException e) {
//            responseObserver.onError(Status.NOT_FOUND
//                                             .withDescription(e.getMessage())
//                                             .asRuntimeException());
//        }
//    }