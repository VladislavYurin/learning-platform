package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.admin.UsersProto;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;

@Component
public class ProtoMapper {

    public static UsersProto.UserInfoDto toProto(UserEntity userEntity) {

        return UsersProto.UserInfoDto.newBuilder()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .setRole(mapRole(userEntity.getRole()))
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setTgNickname(userEntity.getTgNickname())
                .build();
    }

    private static UsersProto.Role mapRole(Role entityRole) {
        return switch (entityRole) {
            case USER -> UsersProto.Role.USER;
            case ADMIN -> UsersProto.Role.ADMIN;
            case MENTOR -> UsersProto.Role.MENTOR;
            default -> UsersProto.Role.USER;
        };
    }
}