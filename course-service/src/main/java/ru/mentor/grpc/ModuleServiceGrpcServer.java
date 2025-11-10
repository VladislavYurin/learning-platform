package ru.mentor.grpc;

import io.grpc.Status;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.DeleteModuleResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.ReactorModuleServiceGrpc.ModuleServiceImplBase;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.service.ModuleService;

/**
 * gRPC - сервер для модулей курсов, принимает запросы от клиента, находящегося в api-gateway.
 * Валидирует gRPC - запросы, логирует вызовы и передает их в сервис
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ModuleServiceGrpcServer extends ModuleServiceImplBase {

    public static final String CREATE_MODULE_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на создание модуля [ название = {} ]"
                    + " в курсе [ ID = {} ] от пользователя с [ ID = {} ]";
    public static final String GET_MODULE_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на получение данных о модуле"
                    + " [ number = {} ] от пользователя с [ ID = {} ]";
    public static final String DELETE_MODULE_REQUEST_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на удаление модуля c"
                    + " [ number = {} ] из курса [ ID = {} ] от пользователя с [ ID = {} ]";
    public static final String IMPORT_MODULE_FROM_FILE_LOG_TEXT =
            "[ rqUID = {} ] Поступил запрос на импорт модуля из файла [ имя файла = {} ]"
                    + " в курс [ ID = {} ] от пользователя с [ ID = {} ]";

    private final ModuleService moduleService;

    /**
     * Принимает gRPC-запросы на добавление модуля
     *
     * @param request - gRPC-запрос с данными модуля
     *
     * @return - gRPC-ответ с данными добавленного модуля
     */
    public Mono<ModuleResponse> createModule(Mono<CreateModuleGrpcRequest> request) {
        return request
                .doOnNext(this::logCreateModuleRequest)
                .flatMap(moduleService::createModule)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запросы на получение модуля курса
     *
     * @param request - gRPC-запрос с данными модуля
     *
     * @return gRPC - ответ с данными модуля
     */
    @Override
    public Mono<ModuleResponse> getModule(Mono<GetModuleRequest> request) {
        return request
                .doOnNext(this::logGetModuleRequest)
                .flatMap(moduleService::getModule)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запросы на удаление модуля
     *
     * @param request - gRPC-запрос с данными удаляемого модуля
     *
     * @return - пустой gRPC-ответ
     */
    @Override
    public Mono<DeleteModuleResponse> deleteModule(Mono<DeleteModuleRequest> request) {
        return request
                .doOnNext(this::logDeleteModuleRequest)
                .flatMap(moduleService::deleteModule)
                .onErrorMap(EntityNotFoundException.class, convertToRuntimeException());
    }

    /**
     * Принимает gRPC-запросы на импорт модуля из файла
     *
     * @param request - gRPC-запрос с данными модуля
     *
     * @return - gRPC-ответ с данными импортированного модуля
     */
    @Override
    public Mono<ModuleResponse> importModuleFromFile(Mono<ImportModuleFromFileRequest> request) {
        return request
                .doOnNext(this::logImportModuleFromFileRequest)
                .flatMap(moduleService::importModuleFromFile)
                .onErrorMap(EntityNotFoundException.class,convertToRuntimeException());
    }

    private void logCreateModuleRequest(CreateModuleGrpcRequest request) {
        log.info(CREATE_MODULE_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getTitle(),
                 request.getCourseId(),
                 request.getSenderId());
    }

    private void logGetModuleRequest(GetModuleRequest request) {
        log.info(GET_MODULE_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getModuleOrderNumber(),
                 request.getSenderId());
    }

    private void logDeleteModuleRequest(DeleteModuleRequest request) {
        log.info(DELETE_MODULE_REQUEST_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getModuleOrderNumber(),
                 request.getCourseId(),
                 request.getSenderId());
    }

    private void logImportModuleFromFileRequest(ImportModuleFromFileRequest request) {
        log.info(IMPORT_MODULE_FROM_FILE_LOG_TEXT,
                 request.getHeader().getRequestId(),
                 request.getFilename(),
                 request.getCourseId(),
                 request.getSenderId());
    }


    private Function<EntityNotFoundException, Throwable> convertToRuntimeException() {
        return e -> Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
