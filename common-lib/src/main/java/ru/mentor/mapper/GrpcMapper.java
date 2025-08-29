package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.admin.UserByIdResponse;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;

/**
 * Класс содержит мапперы для преобразования сущностей gRPC в DTO и обратно.
 *
 */
@Component
public class GrpcMapper {
    /**
     * Преобразует protobuf-ответ {@link UserByIdResponse} в DTO {@link UserInfoDto}.
     * Маппит все необходимые поля из protobuf-модели в доменный объект для работы на REST стороне.
     *
     * @param response содержит сущность-ответ gRPC сервера с данными пользователя
     * @return объект {@link UserInfoDto}.
     */
    public UserInfoDto mapFromProtobufToUserInfoDto(UserByIdResponse response) {

        return UserInfoDto.builder()
                .id(response.getId())
                .username(response.getUsername())
                .role(Role.valueOf(response.getRole().name()))
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .tgNickname(response.getTgNickname())
                .tgChatId(response.getTgChatId())
                .build();
    }

    /**
     * Преобразует JPA-сущность {@link UserEntity} в protobuf-модель {@link UserByIdResponse}.
     *
     * @param userEntity сущность пользователя из базы данных.
     * @return protobuf-объект, содержит данные о пользователе.
     */
    public UserByIdResponse mapFromUserEntityToProtobuf(UserEntity userEntity) {

        return UserByIdResponse.newBuilder()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setRole(ru.mentor.admin.Role.valueOf(userEntity.getRole().name()))
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setTgNickname(userEntity.getTgNickname())
                .setTgChatId(userEntity.getTgChatId() != null ? userEntity.getTgChatId() : 0L)
                .build();
    }
}