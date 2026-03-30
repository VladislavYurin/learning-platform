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
import ru.mentor.mapper.BaseMapper;
import ru.mentor.util.GrpcRequestLogContext;

/**
 * gRPC-сервис для работы с модулями для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminModuleServiceServer extends
        ReactorAdminModuleServiceGrpc.AdminModuleServiceImplBase {

    private final ModuleFacade moduleFacade;

    private final BaseMapper baseMapper;

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
                .flatMap(getModuleRequest -> {
                    String requestId = getModuleRequest.getHeader().getRequestId();

                    GrpcRequestLogContext.withRequestId(requestId, () ->
                            log.debug(
                                    "Получен gRPC запрос на получение модуля: [moduleId={}] [senderId={}]",
                                    getModuleRequest.getModuleId(),
                                    getModuleRequest.getSenderId()
                            )
                    );

                    return moduleFacade.findModuleResponseById(getModuleRequest.getModuleId())
                            .doOnSuccess(response ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.debug(
                                                    "Успешно обработан gRPC запрос на получение модуля: [moduleId={}] [senderId={}]",
                                                    getModuleRequest.getModuleId(),
                                                    getModuleRequest.getSenderId()
                                            )
                                    )
                            )
                            .doOnError(error ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.error(
                                                    "Ошибка обработки gRPC запроса на получение модуля: [moduleId={}] [senderId={}] [cause={}]",
                                                    getModuleRequest.getModuleId(),
                                                    getModuleRequest.getSenderId(),
                                                    GrpcRequestLogContext.buildErrorDescription(error),
                                                    error
                                            )
                                    )
                            );
                })
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
                .flatMap(grpcPageRequest -> {
                    String requestId = grpcPageRequest.getHeader().getRequestId();

                    GrpcRequestLogContext.withRequestId(requestId, () ->
                            log.debug(
                                    "Получен gRPC запрос на получение страницы модулей: [pageNumber={}] [pageSize={}] [senderId={}]",
                                    grpcPageRequest.getPageNumber(),
                                    grpcPageRequest.getPageSize(),
                                    grpcPageRequest.getSenderId()
                            )
                    );

                    return Mono.just(grpcPageRequest)
                            .map(baseMapper::mapGrpcPageRequestToPageRequest)
                            .flatMap(moduleFacade::findAllModulesResponse)
                            .doOnSuccess(response ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.debug(
                                                    "Успешно обработан gRPC запрос на получение страницы модулей: [pageNumber={}] [pageSize={}] [senderId={}]",
                                                    grpcPageRequest.getPageNumber(),
                                                    grpcPageRequest.getPageSize(),
                                                    grpcPageRequest.getSenderId()
                                            )
                                    )
                            )
                            .doOnError(error ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.error(
                                                    "Ошибка обработки gRPC запроса на получение страницы модулей: [pageNumber={}] [pageSize={}] [senderId={}] [cause={}]",
                                                    grpcPageRequest.getPageNumber(),
                                                    grpcPageRequest.getPageSize(),
                                                    grpcPageRequest.getSenderId(),
                                                    GrpcRequestLogContext.buildErrorDescription(error),
                                                    error
                                            )
                                    )
                            );
                })
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

    private Function<EntityNotFoundException, Throwable> convertToRuntimeException() {
        return e -> Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

}
