package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.MenteeProgressDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenteeProgressDtoMapper {
    MenteeProgressDto toApiDto(ru.mentor.dto.MenteeProgressDto menteeProgressDto);

    List<MenteeProgressDto> toApiList(List<ru.mentor.dto.MenteeProgressDto> listMenteeProgressDto);
}
