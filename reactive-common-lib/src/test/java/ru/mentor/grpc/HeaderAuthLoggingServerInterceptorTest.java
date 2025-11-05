package ru.mentor.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.mentor.common.Header;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HeaderAuthLoggingServerInterceptorTest {

    private HeaderAuthLoggingServerInterceptor interceptor;
    private static Descriptors.Descriptor FAKE_REQ_DESC;
    private static Descriptors.FieldDescriptor HEADER_FD;

    @BeforeAll
    static void buildFakeReqDescriptor() throws Descriptors.DescriptorValidationException {
        Descriptors.FileDescriptor headerFile = Header.getDescriptor().getFile();

        DescriptorProtos.FieldDescriptorProto headerField =
                DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("header")
                        .setNumber(1)
                        .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE)
                        .setTypeName(Header.getDescriptor().getFullName())
                        .build();

        DescriptorProtos.DescriptorProto fakeReq =
                DescriptorProtos.DescriptorProto.newBuilder()
                        .setName("FakeReq")
                        .addField(headerField)
                        .build();

        DescriptorProtos.FileDescriptorProto fileProto =
                DescriptorProtos.FileDescriptorProto.newBuilder()
                        .setName("fake_req.proto")
                        .addDependency(headerFile.getName())
                        .addMessageType(fakeReq)
                        .build();

        Descriptors.FileDescriptor fd =
                Descriptors.FileDescriptor.buildFrom(fileProto, new Descriptors.FileDescriptor[]{headerFile});

        FAKE_REQ_DESC = fd.findMessageTypeByName("FakeReq");
        HEADER_FD = FAKE_REQ_DESC.findFieldByName("header");
    }

    @BeforeEach
    void setUp() {
        interceptor = new HeaderAuthLoggingServerInterceptor("server-key", "server-node");
    }

    private static Message reqWithHeader(Header h) {
        DynamicMessage.Builder b = DynamicMessage.newBuilder(FAKE_REQ_DESC);
        if (h != null) {
            b.setField(HEADER_FD, h);
        }
        return b.build();
    }

    @SuppressWarnings("unchecked")
    private static <RespT> ServerCall.Listener<Message> startIntercept(
            HeaderAuthLoggingServerInterceptor itc,
            ServerCall<Message, RespT> call,
            ServerCallHandler<Message, RespT> next
    ) {
        return (ServerCall.Listener<Message>) itc.interceptCall(call, new Metadata(), next);
    }

    @Test
    @SuppressWarnings("unchecked")
    void onMessage_withoutHeader_closesWithUnauthenticated_andNotDelegates() {
        ServerCall<Message, Object> call = Mockito.mock(ServerCall.class);
        ServerCall.Listener<Message> delegate = Mockito.mock(ServerCall.Listener.class);
        ServerCallHandler<Message, Object> next = Mockito.mock(ServerCallHandler.class);
        when(next.startCall(any(ServerCall.class), any(Metadata.class))).thenReturn(delegate);

        var listener = startIntercept(interceptor, call, next);

        Message req = reqWithHeader(null);
        listener.onMessage(req);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        ArgumentCaptor<Metadata> mdCaptor = ArgumentCaptor.forClass(Metadata.class);
        verify(call, times(1)).close(statusCaptor.capture(), mdCaptor.capture());
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
        assertEquals("Unauthorized", statusCaptor.getValue().getDescription());
        verify(delegate, never()).onMessage(any());
        verify(next, times(1)).startCall(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void onMessage_withWrongApiKey_closesWithUnauthenticated_andNotDelegates() {
        ServerCall<Message, Object> call = Mockito.mock(ServerCall.class);
        ServerCall.Listener<Message> delegate = Mockito.mock(ServerCall.Listener.class);
        ServerCallHandler<Message, Object> next = Mockito.mock(ServerCallHandler.class);
        when(next.startCall(any(ServerCall.class), any(Metadata.class))).thenReturn(delegate);

        var listener = startIntercept(interceptor, call, next);

        Header bad = Header.newBuilder()
                .setRequestId("rq-1")
                .setNodeId("client-node")
                .setApiKey("WRONG")
                .build();

        listener.onMessage(reqWithHeader(bad));

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(call, times(1)).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
        assertEquals("Unauthorized", statusCaptor.getValue().getDescription());

        verify(delegate, never()).onMessage(any());
        verify(next, times(1)).startCall(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void onMessage_withBlankApiKey_closesWithUnauthenticated_andNotDelegates() {
        ServerCall<Message, Object> call = Mockito.mock(ServerCall.class);
        ServerCall.Listener<Message> delegate = Mockito.mock(ServerCall.Listener.class);
        ServerCallHandler<Message, Object> next = Mockito.mock(ServerCallHandler.class);
        when(next.startCall(any(ServerCall.class), any(Metadata.class))).thenReturn(delegate);

        var listener = startIntercept(interceptor, call, next);

        Header blank = Header.newBuilder()
                .setRequestId("rq-2")
                .setNodeId("client-node")
                .setApiKey("") // пустой ключ -> невалидно
                .build();

        listener.onMessage(reqWithHeader(blank));

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(call, times(1)).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
        assertEquals("Unauthorized", statusCaptor.getValue().getDescription());

        verify(delegate, never()).onMessage(any());
        verify(next, times(1)).startCall(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void onMessage_withValidHeader_delegatesAndDoesNotClose() {
        ServerCall<Message, Object> call = Mockito.mock(ServerCall.class);
        ServerCall.Listener<Message> delegate = Mockito.mock(ServerCall.Listener.class);
        ServerCallHandler<Message, Object> next = Mockito.mock(ServerCallHandler.class);
        when(next.startCall(any(ServerCall.class), any(Metadata.class))).thenReturn(delegate);

        var listener = startIntercept(interceptor, call, next);

        Header ok = Header.newBuilder()
                .setRequestId("rq-42")
                .setNodeId("client-node")
                .setApiKey("server-key")
                .build();

        Message req = reqWithHeader(ok);

        assertDoesNotThrow(() -> listener.onMessage(req));
        verify(delegate, times(1)).onMessage(req);

        verify(call, never()).close(any(), any());
        verify(next, times(1)).startCall(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void onMessage_serverHasBlankApiKey_closesWithUnauthenticated_andNotDelegates() {
        HeaderAuthLoggingServerInterceptor interceptorWithBlank =
                new HeaderAuthLoggingServerInterceptor("", "server-node");

        ServerCall<Message, Object> call = Mockito.mock(ServerCall.class);
        ServerCall.Listener<Message> delegate = Mockito.mock(ServerCall.Listener.class);
        ServerCallHandler<Message, Object> next = Mockito.mock(ServerCallHandler.class);
        when(next.startCall(any(ServerCall.class), any(Metadata.class))).thenReturn(delegate);

        var listener = (ServerCall.Listener<Message>)
                interceptorWithBlank.interceptCall(call, new Metadata(), next);

        Header clientHeader = Header.newBuilder()
                .setRequestId("rq-blank-server")
                .setNodeId("client-node")
                .setApiKey("anything")
                .build();

        listener.onMessage(reqWithHeader(clientHeader));

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(call, times(1)).close(statusCaptor.capture(), any(Metadata.class));
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
        assertEquals("Unauthorized", statusCaptor.getValue().getDescription());

        verify(delegate, never()).onMessage(any());
        verify(next, times(1)).startCall(any(), any());
    }
}
