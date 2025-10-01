package ru.mentor.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AdminModuleServiceGrpc.AdminModuleServiceBlockingStub;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetAllModulesRequest;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.exception.GrpcRetryException;

/**
 * Клиент для обращения к сервису управления модулями через gRPC
 */
@Component
public class AdminModuleServiceGrpcClient {

    @GrpcClient("admin-module-client")
    private AdminModuleServiceBlockingStub blockingStub;

    /**
     * Возвращает запрошенный модуль
     *
     * @param request
     *         объект, содержащий данные для запроса
     *
     * @return {@link ModuleResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public ModuleResponse getModule(GetModuleRequest request) {
        try {
            return blockingStub.getModule(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getRequestId()
                    ),
                    request.getRequestId()
            );
        }
    }

    /**
     * Возвращает все модули в соответствии с настройками страницы.
     *
     * @param request настройки страницы (номер страницы и размер)
     *
     * @return {@link AllModulesResponse}
     */
    @Retryable(
            retryFor = GrpcRetryException.class,
            maxAttemptsExpression = "${grpc.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${grpc.retry.delay}")
    )
    public AllModulesResponse getAllModules(GetAllModulesRequest request) {
        try {
            return blockingStub.getAllModules(request);
        } catch (Exception e) {
            throw new GrpcRetryException(
                    String.format(
                            "[ requestId = %s ] Ошибка отправки gRPC запроса.",
                            request.getRequestId()
                    ),
                    request.getRequestId()
            );
        }
    }

}
