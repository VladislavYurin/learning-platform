package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.MentorSlotInfoDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorSlotInfoDtoMapper {
    MentorSlotInfoDto toApiDto(ru.mentor.dto.MentorSlotInfoDto commonMentorSlotInfoDto);
    List<MentorSlotInfoDto> toListApiDto(List<ru.mentor.dto.MentorSlotInfoDto> listCommonMentorSlotInfoDto);
}
