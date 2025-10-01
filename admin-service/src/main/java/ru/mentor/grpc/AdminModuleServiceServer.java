package ru.mentor.grpc;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GetAllModulesRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.ReactorAdminModuleServiceGrpc;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;

import java.util.List;

/**
 * gRPC-сервис для работы с модулями для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminModuleServiceServer extends
        ReactorAdminModuleServiceGrpc.AdminModuleServiceImplBase {

    private final ModuleRepository moduleRepository;

    private final CourseRepository courseRepository;

    private final AdminModuleMapper moduleMapper;

    /**
     * Возвращает модуль по ID
     *
     * @param request
     *         gRPC-объект {@link GetModuleRequest} запроса страницы
     */
    @Override
    public Mono<ModuleResponse> getModule(Mono<GetModuleRequest> request) {
        return request
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                        .withDescription(GrpcErrorText.EMPTY_REQUEST)
                        .asRuntimeException()))

                .doOnNext(monoGetModuleRequest -> {
                    log.info(
                            "[ rqUID = {} ] Поступил запрос  на получение данных о модуле [ ID = {} ] от администратора",
                            monoGetModuleRequest.getRequestId(),
                            monoGetModuleRequest.getModuleId()
                    );
                })

                .map(GetModuleRequest::getModuleId)

                .flatMap(moduleRepository::findByIdOrThrow)

                .map(moduleMapper::mapModuleEntityToModuleResponse)

                .onErrorMap(
                        EntityNotFoundException.class, e ->
                                Status.NOT_FOUND
                                        .withDescription(e.getMessage())
                                        .asRuntimeException()
                );
    }

    /**
     * Возвращает gRPC-объект, содержащий список модулей.
     *
     * @param request
     *         gRPC-объект {@link GetAllModulesRequest} запроса всех модулей курса
     */
    @Override
    public Mono<AllModulesResponse> getAllModules(Mono<GetAllModulesRequest> request) {
        return request
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                        .withDescription(GrpcErrorText.EMPTY_REQUEST)
                        .asRuntimeException()))
                .doOnNext(req ->
                        log.info(
                                "[ rqUID = {} ] Поступил запрос на получение списка всех модулей курса " +
                                "courseId ={} от администратора",
                                req.getRequestId(),
                                req.getCourseId()
                        )
                )
                .flatMap(req -> {
                    long courseId = req.getCourseId();
                    Mono<CourseEntity> courseEntityMono =
                            courseRepository.findByIdOrThrow(courseId)
                                            .cache();

                    Mono<List<ModuleResponse>> moduleResponseItemsMono =
                            getModuleResponseItemsMono(courseId, courseEntityMono);

                    Mono<Long> totalMono = moduleRepository.countByCourseId(courseId);

                    return Mono.zip(moduleResponseItemsMono, totalMono)
                            .map(tuple -> {
                                        List<ModuleResponse> moduleResponseList = tuple.getT1();

                                        long total = tuple.getT2();

                                        PageRequest pageable = PageRequest.of(
                                                0, Math.max(1, moduleResponseList.size()));

                                        PageImpl<ModuleResponse> page =
                                                new PageImpl<>(moduleResponseList, pageable, total);

                                        return moduleMapper.mapModuleResponsePageToGrpcAllModulesResponse(page);
                                    }
                            )
                            .onErrorMap(EntityNotFoundException.class,
                                    e -> Status.NOT_FOUND
                                            .withDescription(e.getMessage())
                                            .asRuntimeException()
                            );
                });
    }

    /**
     * Формирует список gRPC-ответов модулей курса.
     *
     * @param courseId
     *          id курса
     *
     * @param courseEntityMono
     *          объект Mono с сущностью курса
     *
     * @return Mono-объект со списком из {@link ModuleResponse}
     */
    private Mono<List<ModuleResponse>> getModuleResponseItemsMono(
            long courseId,
            Mono<CourseEntity> courseEntityMono) {
        return moduleRepository
                .findAllByCourseIdOrderByModuleOrderNumberAsc(courseId)
                .flatMap(
                        module -> courseEntityMono
                                .map(course ->
                                        moduleMapper.mapModuleEntityToGrpcModuleResponse(course, module)
                                )
                )
                .collectList();
    }

//    @Override
//    public void getModule(
//            GetModuleRequest request,
//            StreamObserver<ModuleResponse> responseObserver) {
//
//        String requestId = request.getRequestId();
//        long moduleId = request.getModuleId();
//        log.info(
//                "[ rqUID = {} ] Поступил запрос на получение данных о модуле [ ID = {} ] от администратора",
//                requestId,
//                moduleId
//        );
//
//        try {
//            ModuleEntity courseEntity = moduleRepository.findByIdOrThrow(moduleId);
//            ModuleResponse moduleResponse =
//                    moduleMapper.mapModuleEntityToModuleResponse(courseEntity);
//
//            responseObserver.onNext(moduleResponse);
//            responseObserver.onCompleted();
//
//        } catch (EntityNotFoundException e) {
//            responseObserver.onError(Status.NOT_FOUND
//                                             .withDescription(e.getMessage())
//                                             .asRuntimeException());
//        }
//
//    }

//    @Override
//    public void getAllModules(
//            GrpcPageRequest request,
//            StreamObserver<AllModulesResponse> responseObserver) {
//
//        String requestId = request.getRequestId();
//        log.info(
//                "[ rqUID = {} ] Поступил запрос на получение данных обо всех модулях от администратора",
//                requestId
//        );
//
//        try {
//            PageRequest pageRequest = baseMapper.mapGrpcPageRequestToPageRequest(request);
//            Page<ModuleEntity> moduleEntityPage = moduleRepository.findAll(pageRequest);
//
//            AllModulesResponse allCoursesResponse =
//                    moduleMapper.mapModuleEntityPageToGrpcAllModulesResponse(moduleEntityPage);
//
//            responseObserver.onNext(allCoursesResponse);
//            responseObserver.onCompleted();
//
//        } catch (EntityNotFoundException e) {
//            responseObserver.onError(Status.NOT_FOUND
//                                             .withDescription(e.getMessage())
//                                             .asRuntimeException());
//        }
//
//    }

}
