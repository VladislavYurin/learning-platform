package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(target = "userId", source = "id")
    AuthorResponse userEntityToAuthorResponse(UserEntity userEntity);

    @Mapping(target = "role", source = "userEntity",
            qualifiedByName = "userEntityRoleToUserInfoRole")
    UserInfo userEntityToUserInfo(UserEntity userEntity);

}