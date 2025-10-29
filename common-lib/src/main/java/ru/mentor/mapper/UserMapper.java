package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.common.AuthorResponse;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;

@Component
public class UserMapper {

    public UserInfoDto mapGrpcAuthorResponseToUserInfoDto(AuthorResponse author) {
        return UserInfoDto.builder()
                          .id(author.getUserId())
                          .username(author.getUsername())
                          .role(Role.MENTOR)
                          .firstName(author.getFirstName())
                          .lastName(author.getLastName())
                          .tgNickname(author.getTgNickname())
                          .tgChatId(author.getTgChatId())
                          .build();
    }

    public AuthorResponse mapUserEntityToCourseAuthorResponse(UserEntity userEntity) {
        return AuthorResponse.newBuilder()
                                   .setUserId(userEntity.getId())
                                   .setUsername(userEntity.getUsername())
                                   .setFirstName(userEntity.getFirstName())
                                   .setLastName(userEntity.getLastName())
                                   .setTgNickname(userEntity.getTgNickname())
                                   .setTgChatId(userEntity.getTgChatId())
                                   .build();
    }

}
