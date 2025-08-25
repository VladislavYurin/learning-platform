package ru.mentor.admin_service.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;
import ru.mentor.admin.UsersProto;
import ru.mentor.admin.UsersServiceGrpc;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.ProtoMapper;
import ru.mentor.repository.UserRepository;

@Component
@GrpcService
@RequiredArgsConstructor
public class GrpcUsersService extends UsersServiceGrpc.UsersServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUserById(UsersProto.GetUserByIdRequest request,
                            StreamObserver<UsersProto.GetUserByIdResponse> responseObserver) {

        UserEntity user = userRepository.findByIdOrThrow(request.getId());

        UsersProto.GetUserByIdResponse response = UsersProto.GetUserByIdResponse.newBuilder()
                .setUser(ProtoMapper.toProto(user))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
