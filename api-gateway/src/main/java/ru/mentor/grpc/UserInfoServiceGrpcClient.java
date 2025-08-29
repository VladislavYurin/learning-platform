package ru.mentor.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.mentor.admin.UserByIdRequest;
import ru.mentor.admin.UserByIdResponse;
import ru.mentor.admin.UserInfoServiceGrpc;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoServiceGrpcClient {

    @GrpcClient("admin-service")
    private UserInfoServiceGrpc.UserInfoServiceStub asyncStub;

    public CompletableFuture<UserByIdResponse> getOtherUserInfo(Long userId) {

        CompletableFuture<UserByIdResponse> future = new CompletableFuture<>();

        UserByIdRequest request = UserByIdRequest.newBuilder()
                .setId(userId)
                .build();

        asyncStub.getUserInfoByUserId(request, new StreamObserver<UserByIdResponse>() {
            @Override
            public void onNext(UserByIdResponse response) {
                log.info("gRPC onNext: {}", response);
                future.complete(response);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("gRPC onError: {}", throwable);
                future.completeExceptionally(throwable);
            }

            @Override
            public void onCompleted() {
                log.info("gRPC onCompleted");
            }
        });

        return future;
    }
}
