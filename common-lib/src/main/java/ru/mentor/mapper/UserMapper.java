package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.common.AuthorResponse;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Named("mapGrpcAuthorResponseToUserInfoDto")
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "role", expression = "java(ru.mentor.constant.Role.MENTOR)")
    @Mapping(target = "tgChatId", expression = "java(author.getTgChatId() != 0 ? author.getTgChatId() : null)")
    UserInfoDto mapGrpcAuthorResponseToUserInfoDto(AuthorResponse author);

    @Named("mapUserEntityToCourseAuthorResponse")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "tgChatId", expression = "java(userEntity.getTgChatId() != null ? userEntity.getTgChatId() : 0L)")
    AuthorResponse mapUserEntityToCourseAuthorResponse(UserEntity userEntity);

}
