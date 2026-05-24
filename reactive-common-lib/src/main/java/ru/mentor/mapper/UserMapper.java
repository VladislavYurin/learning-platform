package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.UserInfo;
import ru.mentor.entity.UserEntity;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Named("mapUserEntityToCourseAuthorResponse")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "tgChatId",
            conditionExpression = "java(userEntity.getTgChatId() != null)")
    AuthorResponse mapUserEntityToCourseAuthorResponse(UserEntity userEntity);

    @Named("mapUserEntityToUserInfo")
    @Mapping(target = "role", source = "userEntity",
            qualifiedByName = "userEntityRoleToUserInfoRole")
    @Mapping(target = "tgChatId",
            conditionExpression = "java(userEntity.getTgChatId() != null)")
    UserInfo mapUserEntityToUserInfo(UserEntity userEntity);

}
