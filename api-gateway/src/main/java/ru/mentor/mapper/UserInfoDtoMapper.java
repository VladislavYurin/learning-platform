package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.UserInfoDto;

@Mapper(componentModel = "spring")
public interface UserInfoDtoMapper {
    UserInfoDto toApiDto(ru.mentor.dto.UserInfoDto userInfoDto);
}
