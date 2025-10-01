package ru.mentor.grpc;

import io.grpc.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetAllModulesRequest;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.ReactorAdminModuleServiceGrpc;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;

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
                       .doOnNext(getModuleRequest ->
                                         log.info(
                                                 "[ rqUID = {} ] Поступил запрос  на получение данных о модуле "
                                                         + "[ ID = {} ] от администратора [ ID = {} ]",
                                                 getModuleRequest.getRequestId(),
                                                 getModuleRequest.getModuleId(),
                                                 getModuleRequest.getAdminUserId()
                                         ))
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
                       .doOnNext(getAllModulesRequest ->
                                         log.info(
                                                 "[ rqUID = {} ] Поступил запрос на получение списка всех модулей курса "
                                                         + "[ ID = {} ] от администратора [ ID = {} ]",
                                                 getAllModulesRequest.getRequestId(),
                                                 getAllModulesRequest.getCourseId(),
                                                 getAllModulesRequest.getAdminUserId()
                                         ))
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
                                                       new PageImpl<>(
                                                               moduleResponseList,
                                                               pageable,
                                                               total
                                                       );
                                               return moduleMapper.mapModuleResponsePageToGrpcAllModulesResponse(
                                                       page);
                                           }
                                      )
                                      .onErrorMap(
                                              EntityNotFoundException.class,
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
     *         id курса
     * @param courseEntityMono
     *         объект Mono с сущностью курса
     *
     * @return Mono-объект со списком из {@link ModuleResponse}
     */
    private Mono<List<ModuleResponse>> getModuleResponseItemsMono(
            long courseId,
            Mono<CourseEntity> courseEntityMono) {
        return moduleRepository
                       .findAllByCourseIdOrderByModuleOrderNumberAsc(courseId)
                       .flatMap(module -> courseEntityMono.map(
                                        course -> moduleMapper.mapModuleEntityToGrpcModuleResponse(
                                                course,
                                                module
                                        )
                                )
                       )
                       .collectList();
    }

}
