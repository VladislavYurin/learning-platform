package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.gateway.model.PageMentorSlotInfoDto;

@Mapper(componentModel = "spring")
public interface PageMentorSlotInfoDtoMapper {
    PageMentorSlotInfoDto toApiDto(Page<MentorSlotInfoDto> page);
}
