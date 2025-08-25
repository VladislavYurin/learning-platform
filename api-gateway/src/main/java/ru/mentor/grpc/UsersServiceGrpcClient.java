package ru.mentor.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.mentor.admin.UsersProto;
import ru.mentor.admin.UsersServiceGrpc;

@Service
public class UsersServiceGrpcClient {

    @GrpcClient("admin-service")
    private UsersServiceGrpc.UsersServiceBlockingStub stub;

    public UsersProto.UserInfoDto getUserById(Long id) {

        var request = UsersProto.GetUserByIdRequest.newBuilder()
                .setId(id)
                .build();

        return stub.getUserById(request).getUser();
    }
}