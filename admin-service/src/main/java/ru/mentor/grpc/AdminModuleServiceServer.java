package ru.mentor.grpc;

import io.grpc.Status;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.admin.ReactorAdminModuleServiceGrpc;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.facade.ModuleFacade;

/**
 * gRPC-сервис для работы с модулями для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminModuleServiceServer extends
        ReactorAdminModuleServiceGrpc.AdminModuleServiceImplBase {

    private final ModuleFacade moduleFacade;

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
                                "[ rqUID = {} ] Поступил запрос на получение модуля [ ID = {} ] от администратора [ ID = {} ]",
                                getModuleRequest.getRequestId(),
                                getModuleRequest.getModuleId(),
                                getModuleRequest.getSenderId()
                        ))
                .flatMap(req -> moduleFacade.findModuleResponseByCourseId(req.getModuleId()))
                .onErrorMap(
                        EntityNotFoundException.class,
                        e -> Status.NOT_FOUND
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
        return request.switchIfEmpty(toInvalidArgumentError())
                       .doOnNext(AdminModuleServiceServer::recordToLogGetAllModulesRequest)
                       .flatMap(req -> moduleFacade.findAllModulesAndMapToAllModulesResponse(req.getCourseId()))
                       .onErrorMap(
                               EntityNotFoundException.class,
                               convertToRuntimeException()
                       );
    }

    private Function<EntityNotFoundException, Throwable> convertToRuntimeException() {
        return e -> Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    private static void recordToLogGetAllModulesRequest(GetAllModulesRequest getAllModulesRequest) {
        log.info(
                "[ rqUID = {} ] Поступил запрос на получение списка всех модулей курса "
                        + "[ ID = {} ] от администратора [ ID = {} ]",
                getAllModulesRequest.getRequestId(),
                getAllModulesRequest.getCourseId(),
                getAllModulesRequest.getSenderId()
        );
    }

    private Mono<GetAllModulesRequest> toInvalidArgumentError() {
        return Mono.error(Status.INVALID_ARGUMENT
                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                  .asRuntimeException());
    }

}
