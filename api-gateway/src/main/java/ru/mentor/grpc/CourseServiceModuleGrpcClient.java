package ru.mentor.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.DeleteModuleResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.ModuleServiceGrpc.ModuleServiceBlockingStub;
import ru.mentor.exception.GrpcRetryException;

/**
 * gRPC-клиент, отправляет запросы в course-service для работы с модулями
 */
@Slf4j
@Component
@Retryable(
        retryFor = GrpcRetryException.class,
        maxAttemptsExpression = "${grpc.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
)
public class CourseServiceModuleGrpcClient {

    @GrpcClient("module-client")
    private ModuleServiceBlockingStub moduleServiceBlockingStub;

    /**
     * Отправляет gRPC-запрос для получения модуля курса
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return - ДТО с данными запрошенного модуля
     */
    public ModuleResponse getModule(GetModuleRequest request) {
        try {
            return moduleServiceBlockingStub.getModule(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для получения всех модулей курса
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return ДТО с данными всех модулей
     */
    public AllModulesResponse getAllModules(GrpcPageRequest request) {
        try {
            return moduleServiceBlockingStub.getAllModules(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для импорта модуля из файла
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return ДТО с данными импортированного модуля
     */
    public ModuleResponse importModuleFromMarkdown(ImportModuleFromFileRequest request) {
        try {
            return moduleServiceBlockingStub.importModuleFromFile(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для создания нового модуля в курсе
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return - ДТО с данными созданного модуля
     */
    public ModuleResponse createModule(CreateModuleGrpcRequest request) {
        try {
            return moduleServiceBlockingStub.createModule(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

    /**
     * Отправляет gRPC-запрос для удаления модуля в курсе
     *
     * @param request
     *         - ДТО с данными запроса
     *
     * @return - пустой gRPC-ответ
     */
    public DeleteModuleResponse deleteModule(DeleteModuleRequest request) {
        try {
            return moduleServiceBlockingStub.deleteModule(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getHeader().getRequestId()
                    ),
                    request.getHeader().getRequestId()
            );
        }
    }

}