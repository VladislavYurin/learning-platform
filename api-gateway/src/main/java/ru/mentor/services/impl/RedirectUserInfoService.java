package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.grpc.UserInfoServiceGrpcClient;
import ru.mentor.mapper.GrpcMapper;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectUserInfoService {

    private final UserInfoServiceGrpcClient client;
    private final GrpcMapper grpcMapper;

    public CompletableFuture<UserInfoDto> getOtherUserInfo(Long userId) {
        return client.getOtherUserInfo(userId)
                        .thenApply(grpcMapper::mapFromProtobufToUserInfoDto);
    }
}