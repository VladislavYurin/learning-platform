package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.admin.AuthorResponse;
import ru.mentor.calendar.UserInfo;
import ru.mentor.entity.UserEntity;

@Component
public class UserMapper {

    public AuthorResponse mapUserEntityToCourseAuthorResponse(UserEntity userEntity) {
        AuthorResponse.Builder builder = AuthorResponse.newBuilder()
                                                       .setUserId(userEntity.getId())
                                                       .setUsername(userEntity.getUsername())
                                                       .setFirstName(userEntity.getFirstName())
                                                       .setLastName(userEntity.getLastName())
                                                       .setTgNickname(userEntity.getTgNickname());

        if (userEntity.getTgChatId() != null) {
            builder.setTgChatId(userEntity.getTgChatId());
        }

        return builder.build();
    }

    public UserInfo mapUserEntityToUserInfo(UserEntity userEntity) {
        UserInfo.Builder builder = UserInfo.newBuilder()
                                           .setId(userEntity.getId())
                                           .setUsername(userEntity.getUsername())
                                           .setRole(UtilMapper.userEntityRoleToUserInfoRole(
                                                   userEntity))
                                           .setFirstName(userEntity.getFirstName())
                                           .setLastName(userEntity.getLastName())
                                           .setTgNickname(userEntity.getTgNickname());

        if (userEntity.getTgChatId() != null) {
            builder.setTgChatId(userEntity.getTgChatId());
        }

        return builder.build();
    }

}
