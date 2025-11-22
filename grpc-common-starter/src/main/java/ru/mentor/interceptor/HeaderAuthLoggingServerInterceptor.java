package ru.mentor.interceptor;

import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import ru.mentor.common.Header;
import ru.mentor.config.GrpcHeaderProperties;

/**
 * gRPC-серверный интерцептор: базовое логирование + проверка apiKey.
 * При фейле закрывает вызов UNAUTHENTICATED и не передаёт дальше.
 */
@Slf4j
@GrpcGlobalServerInterceptor
public class HeaderAuthLoggingServerInterceptor implements ServerInterceptor {

    private static final String NO_REQUEST_ID = "-";
    private static final String HEADER_FIELD = "header";
    private static final String UNAUTHORIZED_MSG = "Unauthorized";
    private final String serverApiKey;
    private final String serverNodeId;

    public HeaderAuthLoggingServerInterceptor(GrpcHeaderProperties props) {
        this.serverApiKey = props.getApiKey();
        this.serverNodeId = props.getNodeId();
    }

    /**
     * Оборачивает серверный вызов, логирует и проверяет запрос до передачи в целевой обработчик.
     *
     * @param call
     *         исходный gRPC-вызов
     * @param metadata
     *         метаданные запроса
     * @param next
     *         следующий обработчик
     * @param <ReqT>
     *         тип входящего сообщения
     * @param <RespT>
     *         тип исходящего сообщения
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> next
    ) {
        ServerCall.Listener<ReqT> listener = next.startCall(call, metadata);

        return new SimpleForwardingServerCallListener<>(listener) {
            private boolean closedByAuthFail = false;

            @Override
            public void onMessage(ReqT message) {
                Header header = extractHeader(message);
                if (header == null) {
                    closedByAuthFail = true;
                    closeUnauthenticated(call, NO_REQUEST_ID);
                    return;
                }

                final String requestId = header.getRequestId();
                final String clientNodeId = header.getNodeId();
                final String apiKey = header.getApiKey();

                logInboundCall(requestId, clientNodeId);

                if (isApiKeyInvalid(apiKey)) {
                    closedByAuthFail = true;
                    closeUnauthenticated(call, requestId);
                    return;
                }

                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                if (closedByAuthFail) {
                    return;
                }
                super.onHalfClose();
            }
        };
    }

    /** Получает header через protobuf-рефлексию. */
    private static Header extractHeader(Object req) {
        if (req instanceof Message msg) {
            Descriptors.FieldDescriptor fd =
                    msg.getDescriptorForType().findFieldByName(HEADER_FIELD);
            if (fd == null || !msg.hasField(fd)) {
                return null;
            }

            Object val = msg.getField(fd);

            if (val instanceof Header h) {
                return h;
            }
            if (val instanceof Message anyMsg
                    && anyMsg.getDescriptorForType().equals(Header.getDescriptor())) {
                try {
                    return Header.parseFrom(anyMsg.toByteString());
                } catch (InvalidProtocolBufferException e) {
                    log.error("Failed to parse protobuf 'header' field to Header", e);
                }
            }
        }

        return null;
    }

    /** Лог входящего вызова. */
    private void logInboundCall(String requestId, String clientNodeId) {
        log.debug(
                "[ requestId={} ][ clientNodeId={} ][ serverNodeId={} ][ ARRIVED ]",
                requestId, clientNodeId, serverNodeId
        );
    }

    /** Проверяет входящий apiKey. */
    private boolean isApiKeyInvalid(String apiKey) {
        return apiKey == null || apiKey.isBlank() || !Objects.equals(apiKey, serverApiKey);
    }

    /** Закрывает вызов UNAUTHENTICATED. */
    private void closeUnauthenticated(ServerCall<?, ?> call, String requestId) {
        log.error(
                "[ requestId={} ][ serverNodeId={} ][ AUTH_FAIL ][ status=UNAUTHENTICATED ]",
                requestId, serverNodeId
        );
        call.close(Status.UNAUTHENTICATED.withDescription(UNAUTHORIZED_MSG), new Metadata());
    }

}
