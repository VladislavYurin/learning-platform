package ru.mentor.admin.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;
import ru.mentor.admin.UserByIdRequest;
import ru.mentor.admin.UserByIdResponse;
import ru.mentor.admin.UserInfoServiceGrpc;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.GrpcMapper;
import ru.mentor.repository.UserRepository;

/**
 * Сервис для получения информации о пользователе по его id
 *
 * Реализует абстрактный класс {@link UserInfoServiceGrpc.UserInfoServiceImplBase}, серверная часть gRPC
 *
 * {@link GrpcService} - регистрирует класс как gRPC - бин.
 */

@Component
@GrpcService
@RequiredArgsConstructor
public class UserInfoService extends UserInfoServiceGrpc.UserInfoServiceImplBase {

    private final UserRepository userRepository;
    private final GrpcMapper grpcMapper;

    /**
     * Обрабатывает gRPC - запрос на получение информации о пользователе по его идентификатору
     *
     * Ищет пользователя в {@link UserRepository}, преобразует с помощью {@link GrpcMapper} в
     * protobuf-ответ и отправляет его клиенту через {@link StreamObserver}.
     * @param request gRPC - запрос
     * @param responseObserver - поток для ответов клиенту
     */
    @Override
    public void getUserInfoByUserId(UserByIdRequest request,
                            StreamObserver<UserByIdResponse> responseObserver) {

        UserEntity userEntity = userRepository.findByIdOrThrow(request.getId());

        UserByIdResponse response = grpcMapper.mapFromUserEntityToProtobuf(userEntity);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
