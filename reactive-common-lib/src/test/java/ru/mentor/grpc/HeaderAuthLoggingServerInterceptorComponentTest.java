package ru.mentor.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import io.grpc.BindableService;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentor.common.Header;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeaderAuthLoggingServerInterceptorComponentTest {

    private static Descriptors.Descriptor FAKE_REQ_DESC;
    private static Descriptors.FieldDescriptor HEADER_FD;
    private static MethodDescriptor<Message, Empty> PING_METHOD;

    private Server server;
    private int port;

    // ---------- Static wiring ----------
    @BeforeAll
    static void setupDescriptor() throws Descriptors.DescriptorValidationException {
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

        PING_METHOD = MethodDescriptor.<Message, Empty>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName("test.FakeService", "Ping"))
                .setRequestMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(FAKE_REQ_DESC)))
                .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
                .build();
    }

    // ---------- Server lifecycle ----------
    @BeforeEach
    void startServer() throws IOException {
        HeaderAuthLoggingServerInterceptor interceptor =
                new HeaderAuthLoggingServerInterceptor("server-key", "test-node");

        server = NettyServerBuilder.forAddress(new InetSocketAddress("localhost", 0))
                .directExecutor()
                .addService(new FakeGrpcService())
                .intercept(interceptor)
                .build()
                .start();

        port = server.getPort();
    }

    @AfterEach
    void stopServer() throws InterruptedException {
        if (server != null) {
            server.shutdownNow();
            server.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ---------- Helpers ----------
    private static Message reqWith(Header h) {
        DynamicMessage.Builder b = DynamicMessage.newBuilder(FAKE_REQ_DESC);
        if (h != null) {
            b.setField(HEADER_FD, h);
        }
        return b.build();
    }

    private ManagedChannel newChannel() {
        return ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext()
                .directExecutor()
                .build();
    }

    private static Empty callPing(ManagedChannel ch, Message req) {
        return ClientCalls.blockingUnaryCall(ch, PING_METHOD, CallOptions.DEFAULT, req);
    }

    // ---------- Tests ----------
    @Test
    void ok_withValidHeader_passesThroughInterceptor() throws InterruptedException {
        final ManagedChannel ch = newChannel();
        try {
            Header hdr = Header.newBuilder()
                    .setRequestId("rq-OK")
                    .setNodeId("client-node")
                    .setApiKey("server-key")
                    .build();

            Empty resp = callPing(ch, reqWith(hdr));
            assertNotNull(resp);
        } finally {
            ch.shutdownNow();
            ch.awaitTermination(3, TimeUnit.SECONDS);
        }
    }

    @Test
    void fail_withoutHeader_getsUnauthenticated() throws InterruptedException {
        final ManagedChannel ch = newChannel();
        try {
            StatusRuntimeException ex = assertThrows(
                    StatusRuntimeException.class,
                    () -> callPing(ch, reqWith(null))
            );
            assertEquals(Status.Code.UNAUTHENTICATED, ex.getStatus().getCode());
            assertEquals("Unauthorized", ex.getStatus().getDescription());
        } finally {
            ch.shutdownNow();
            ch.awaitTermination(3, TimeUnit.SECONDS);
        }
    }

    @Test
    void fail_withWrongApiKey_getsUnauthenticated() throws InterruptedException {
        final ManagedChannel ch = newChannel();
        try {
            Header bad = Header.newBuilder()
                    .setRequestId("rq-BAD")
                    .setNodeId("client-node")
                    .setApiKey("WRONG")
                    .build();

            StatusRuntimeException ex = assertThrows(
                    StatusRuntimeException.class,
                    () -> callPing(ch, reqWith(bad))
            );
            assertEquals(Status.Code.UNAUTHENTICATED, ex.getStatus().getCode());
            assertEquals("Unauthorized", ex.getStatus().getDescription());
        } finally {
            ch.shutdownNow();
            ch.awaitTermination(3, TimeUnit.SECONDS);
        }
    }

    @Test
    void fail_withBlankClientApiKey_getsUnauthenticated() throws InterruptedException {
        final ManagedChannel ch = newChannel();
        try {
            Header blank = Header.newBuilder()
                    .setRequestId("rq-BLANK-CLIENT")
                    .setNodeId("client-node")
                    .setApiKey("") // пусто
                    .build();

            StatusRuntimeException ex = assertThrows(
                    StatusRuntimeException.class,
                    () -> callPing(ch, reqWith(blank))
            );
            assertEquals(Status.Code.UNAUTHENTICATED, ex.getStatus().getCode());
            assertEquals("Unauthorized", ex.getStatus().getDescription());
        } finally {
            ch.shutdownNow();
            ch.awaitTermination(3, TimeUnit.SECONDS);
        }
    }

    @Test
    void fail_whenServerApiKeyIsBlank_getsUnauthenticated() throws Exception {
        // Поднимаем отдельный сервер с ПУСТЫМ serverApiKey
        final HeaderAuthLoggingServerInterceptor blankServerKeyInterceptor =
                new HeaderAuthLoggingServerInterceptor("", "test-node");

        final Server tmpServer = NettyServerBuilder.forAddress(new InetSocketAddress("localhost", 0))
                .directExecutor()
                .addService(new FakeGrpcService())
                .intercept(blankServerKeyInterceptor)
                .build()
                .start();

        try {
            final int tmpPort = tmpServer.getPort();
            final ManagedChannel chRef = ManagedChannelBuilder.forAddress("localhost", tmpPort)
                    .usePlaintext()
                    .directExecutor()
                    .build();

            try {
                Header clientHdr = Header.newBuilder()
                        .setRequestId("rq-SERVER-BLANK")
                        .setNodeId("client-node")
                        .setApiKey("anything")
                        .build();

                StatusRuntimeException ex = assertThrows(
                        StatusRuntimeException.class,
                        () -> callPing(chRef, reqWith(clientHdr))
                );
                assertEquals(Status.Code.UNAUTHENTICATED, ex.getStatus().getCode());
                assertEquals("Unauthorized", ex.getStatus().getDescription());
            } finally {
                chRef.shutdownNow();
                chRef.awaitTermination(3, TimeUnit.SECONDS);
            }
        } finally {
            tmpServer.shutdownNow();
            tmpServer.awaitTermination();
        }
    }

    // ---------- Minimal fake service ----------
    static class FakeGrpcService implements BindableService {
        @Override
        public ServerServiceDefinition bindService() {
            return ServerServiceDefinition.builder("test.FakeService")
                    .addMethod(
                            PING_METHOD,
                            ServerCalls.asyncUnaryCall((Message req, StreamObserver<Empty> obs) -> {
                                // Если интерцептор пропустил — OK
                                obs.onNext(Empty.getDefaultInstance());
                                obs.onCompleted();
                            })
                    )
                    .build();
        }
    }
}
