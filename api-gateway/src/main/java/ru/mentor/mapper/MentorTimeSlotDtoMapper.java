package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.MentorTimeSlotDto;

@Mapper(componentModel = "spring")
public interface MentorTimeSlotDtoMapper {
    MentorTimeSlotDto toApiDto(ru.mentor.dto.MentorTimeSlotDto mentorTimeSlotDto);
}
