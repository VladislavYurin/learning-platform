package ru.mentor.grpc;

import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mentor.common.Header;

import java.util.Objects;

/**
 * gRPC-серверный интерцептор: базовое логирование + проверка apiKey.
 * При фейле закрывает вызов UNAUTHENTICATED и не передаёт дальше.
 */
@Slf4j
@Component
@GrpcGlobalServerInterceptor
public class HeaderAuthLoggingServerInterceptor implements ServerInterceptor {

    private final String serverApiKey;
    private final String serverNodeId;

    public HeaderAuthLoggingServerInterceptor(
            @Value("${security.api-key}") String serverApiKey,
            @Value("${service.node-id:unknown-node}") String serverNodeId
    ) {
        this.serverApiKey = serverApiKey;
        this.serverNodeId = serverNodeId;
    }

    /**
     * Точка входа серверного gRPC-интерцептора.
     * Метод получает исходный {@link ServerCall.Listener}, создаваемый gRPC
     * через {@code next.startCall(...)}, и возвращает обёртку
     * {@link SimpleForwardingServerCallListener}, позволяющую перехватить
     * входящее сообщение в {@code onMessage(..)} до передачи в целевой обработчик.
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);

        return new SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onMessage(ReqT message) {
                Header header = extractHeader(message);
                if (header == null) {
                    closeUnauthenticated(call, "-");
                    return;
                }

                final String requestId    = header.getRequestId();
                final String clientNodeId = header.getNodeId();
                final String apiKey       = header.getApiKey();

                logInboundCall(requestId, clientNodeId);

                if (!isApiKeyValid(apiKey)) {
                    closeUnauthenticated(call, requestId);
                    return;
                }

                // После валидации дальнейшая обработка делегируется исходному ServerCall.Listener
                super.onMessage(message);
            }
        };
    }

    /** Получает header через protobuf-рефлексию. */
    private static Header extractHeader(Object req) {
        if (!(req instanceof Message msg)) return null;

        Descriptors.FieldDescriptor fd = msg.getDescriptorForType().findFieldByName("header");
        if (fd == null || !msg.hasField(fd)) return null;

        Object val = msg.getField(fd);

        if (val instanceof Header h) return h;
        if (val instanceof Message anyMsg) {
            if (anyMsg.getDescriptorForType().equals(Header.getDescriptor())) {
                try {
                    return Header.parseFrom(anyMsg.toByteString());
                } catch (InvalidProtocolBufferException e) {
                    log.warn("Failed to parse protobuf 'header' field to Header", e);
                }
            }
        }
        return null;
    }

    /** Лог входящего вызова. */
    private void logInboundCall(String requestId, String clientNodeId) {
        log.info("[ requestId={} ][ clientNodeId={} ][ serverNodeId={} ][ ARRIVED ]",
                requestId, clientNodeId, serverNodeId);
    }

    /** Проверяет серверную конфигурацию и входящий apiKey. */
    private boolean isApiKeyValid(String apiKey) {
        return serverApiKey != null
                && !serverApiKey.isBlank()
                && apiKey != null
                && !apiKey.isBlank()
                && Objects.equals(apiKey, serverApiKey);
    }

    /** Закрывает вызов UNAUTHENTICATED. */
    private void closeUnauthenticated(ServerCall<?, ?> call, String requestId) {
        log.warn("[ requestId={} ][ serverNodeId={} ][ AUTH_FAIL ][ status=UNAUTHENTICATED ]",
                requestId, serverNodeId);
        call.close(Status.UNAUTHENTICATED.withDescription("Unauthorized"), new Metadata());
    }
}
