package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.admin.UsersProto;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;

@Component
public class UserInfoMapper {

    public UserInfoDto fromProtoUserInfo(UsersProto.UserInfoDto proto) {
        return UserInfoDto.builder()
                .id(proto.getId())
                .username(proto.getUsername())
                .role(Role.valueOf(proto.getRole().name()))
                .firstName(proto.getFirstName())
                .lastName(proto.getLastName())
                .tgNickname(proto.getTgNickname())
                .build();
    }
}
