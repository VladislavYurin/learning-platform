package ru.mentor.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.config.GrpcHeaderProperties;
import ru.mentor.testUtil.TestConstantHolder;

class HeaderAuthLoggingServerInterceptorTest {

    private static final String UNAUTHORIZED_MSG = "Unauthorized";

    private HeaderAuthLoggingServerInterceptor createInterceptor(String nodeId, String apiKey) {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(nodeId);
        props.setApiKey(apiKey);
        return new HeaderAuthLoggingServerInterceptor(props);
    }

    @Test
    void interceptCall_validHeaderAndApiKey_delegatesAndDoesNotClose() {

        HeaderAuthLoggingServerInterceptor interceptor = createInterceptor(
                TestConstantHolder.NODE_ID,
                TestConstantHolder.API_KEY
        );

        @SuppressWarnings("unchecked")
        ServerCall<Object, Object> call = Mockito.mock(ServerCall.class);
        @SuppressWarnings("unchecked")
        ServerCallHandler<Object, Object> next = Mockito.mock(ServerCallHandler.class);
        @SuppressWarnings("unchecked")
        ServerCall.Listener<Object> delegateListener = Mockito.mock(ServerCall.Listener.class);

        Mockito.when(next.startCall(ArgumentMatchers.any(), ArgumentMatchers.any()))
               .thenReturn(delegateListener);

        Metadata metadata = new Metadata();

        Header header = Header.newBuilder()
                              .setRequestId(TestConstantHolder.REQUEST_ID)
                              .setNodeId(TestConstantHolder.REMOTE_NODE_ID)
                              .setApiKey(TestConstantHolder.API_KEY)
                              .build();

        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                                                         .setHeader(header)
                                                         .build();

        ServerCall.Listener<Object> listener = interceptor.interceptCall(call, metadata, next);
        listener.onMessage(request);

        Mockito.verify(next).startCall(call, metadata);
        Mockito.verify(delegateListener).onMessage(request);
        Mockito.verify(call, Mockito.never())
               .close(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void interceptCall_noHeader_closesWithUnauthenticatedAndDoesNotDelegate() {

        HeaderAuthLoggingServerInterceptor interceptor = createInterceptor(
                TestConstantHolder.NODE_ID,
                TestConstantHolder.API_KEY
        );

        @SuppressWarnings("unchecked")
        ServerCall<Object, Object> call = Mockito.mock(ServerCall.class);
        @SuppressWarnings("unchecked")
        ServerCallHandler<Object, Object> next = Mockito.mock(ServerCallHandler.class);
        @SuppressWarnings("unchecked")
        ServerCall.Listener<Object> delegateListener = Mockito.mock(ServerCall.Listener.class);

        Mockito.when(next.startCall(ArgumentMatchers.any(), ArgumentMatchers.any()))
               .thenReturn(delegateListener);

        Metadata metadata = new Metadata();

        BookTimeSlotRequest requestWithoutHeader = BookTimeSlotRequest.newBuilder().build();

        ServerCall.Listener<Object> listener = interceptor.interceptCall(call, metadata, next);
        listener.onMessage(requestWithoutHeader);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);

        Mockito.verify(call).close(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status = statusCaptor.getValue();

        Assertions.assertEquals(Status.UNAUTHENTICATED.getCode(), status.getCode());
        Assertions.assertEquals(UNAUTHORIZED_MSG, status.getDescription());

        Mockito.verify(delegateListener, Mockito.never())
               .onMessage(ArgumentMatchers.any());
    }

    @Test
    void interceptCall_invalidApiKey_closesWithUnauthenticatedAndDoesNotDelegate() {

        HeaderAuthLoggingServerInterceptor interceptor = createInterceptor(
                TestConstantHolder.NODE_ID,
                TestConstantHolder.API_KEY
        );

        @SuppressWarnings("unchecked")
        ServerCall<Object, Object> call = Mockito.mock(ServerCall.class);
        @SuppressWarnings("unchecked")
        ServerCallHandler<Object, Object> next = Mockito.mock(ServerCallHandler.class);
        @SuppressWarnings("unchecked")
        ServerCall.Listener<Object> delegateListener = Mockito.mock(ServerCall.Listener.class);

        Mockito.when(next.startCall(ArgumentMatchers.any(), ArgumentMatchers.any()))
               .thenReturn(delegateListener);

        Metadata metadata = new Metadata();

        Header headerWithWrongKey = Header.newBuilder()
                                          .setRequestId(TestConstantHolder.REQUEST_ID)
                                          .setNodeId(TestConstantHolder.REMOTE_NODE_ID)
                                          .setApiKey(TestConstantHolder.INVALID_API_KEY)
                                          .build();

        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                                                         .setHeader(headerWithWrongKey)
                                                         .build();

        ServerCall.Listener<Object> listener = interceptor.interceptCall(call, metadata, next);
        listener.onMessage(request);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);

        Mockito.verify(call).close(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status = statusCaptor.getValue();

        Assertions.assertEquals(Status.UNAUTHENTICATED.getCode(), status.getCode());
        Assertions.assertEquals(UNAUTHORIZED_MSG, status.getDescription());

        Mockito.verify(delegateListener, Mockito.never())
               .onMessage(ArgumentMatchers.any());
    }

}
