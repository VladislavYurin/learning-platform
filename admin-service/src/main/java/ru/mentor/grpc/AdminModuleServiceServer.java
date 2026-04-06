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
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.facade.ModuleFacade;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.ReactiveBaseMapper;

/**
 * gRPC-сервис для работы с модулями для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminModuleServiceServer extends
        ReactorAdminModuleServiceGrpc.AdminModuleServiceImplBase {

    public static final String GET_MODULE_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос на получение модуля [ ID = {} ] от администратора [ ID = {} ]";

    public static final String GET_ALL_MODULES_REQUEST_LOG_TEXT = """
            [ requestId = {} ] Поступил запрос на получение страницы модулей \
            [ страница={} ], [ размер={} ] от администратора [ ID = {} ]""";

    private final ModuleFacade moduleFacade;

    private final ReactiveBaseMapper reactiveBaseMapper;

    /**
     * Возвращает модуль по ID
     *
     * @param request
     *         gRPC-объект {@link GetModuleRequest} запроса страницы
     */
    @Override
    public Mono<ModuleResponse> getModule(Mono<GetModuleRequest> request) {
        return request
                .switchIfEmpty(toInvalidArgumentError())
                .doOnNext(this::recordGetModuleRequestToLog)
                .flatMap(req -> moduleFacade.findModuleResponseById(req.getModuleId()))
                .onErrorMap(
                        EntityNotFoundException.class,
                        convertToRuntimeException()
                );
    }

    /**
     * Возвращает gRPC-объект, содержащий список модулей.
     *
     * @param pageRequest
     *         gRPC-объект {@link GetAllModulesRequest} запроса всех модулей курса
     */
    @Override
    public Mono<AllModulesResponse> getAllModules(Mono<GrpcPageRequest> pageRequest) {
        return pageRequest.switchIfEmpty(toInvalidArgumentError())
                          .doOnNext(this::recordGetAllModulesRequestToLog)
                          .map(reactiveBaseMapper::mapGrpcPageRequestToPageRequest)
                          .flatMap(moduleFacade::findAllModulesResponse)
                          .onErrorMap(
                                  EntityNotFoundException.class,
                                  convertToRuntimeException()
                          );
    }

    private <T> Mono<T> toInvalidArgumentError() {
        return Mono.error(Status.INVALID_ARGUMENT
                                  .withDescription(GrpcErrorText.EMPTY_REQUEST)
                                  .asRuntimeException());
    }

    private void recordGetModuleRequestToLog(GetModuleRequest getModuleRequest) {
        log.info(
                GET_MODULE_REQUEST_LOG_TEXT,
                getModuleRequest.getHeader().getRequestId(),
                getModuleRequest.getModuleId(),
                getModuleRequest.getSenderId()
        );
    }

    private void recordGetAllModulesRequestToLog(GrpcPageRequest grpcPageRequest) {
        log.info(
                GET_ALL_MODULES_REQUEST_LOG_TEXT,
                grpcPageRequest.getHeader().getRequestId(),
                grpcPageRequest.getPageNumber(),
                grpcPageRequest.getPageSize(),
                grpcPageRequest.getSenderId()
        );
    }

    private Function<EntityNotFoundException, Throwable> convertToRuntimeException() {
        return e -> Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

}
