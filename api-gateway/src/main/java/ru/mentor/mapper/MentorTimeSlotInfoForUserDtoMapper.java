package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.MentorTimeSlotInfoForUserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorTimeSlotInfoForUserDtoMapper {
    MentorTimeSlotInfoForUserDto toApiDto(ru.mentor.dto.MentorTimeSlotInfoForUserDto commonMentorTimeSlotInfoForUserDto);
    List<MentorTimeSlotInfoForUserDto> toListApiDto(List<ru.mentor.dto.MentorTimeSlotInfoForUserDto> listCommonMentorTimeSlotInfoForUserDto);
}
