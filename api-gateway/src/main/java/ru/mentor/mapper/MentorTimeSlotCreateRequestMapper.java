package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.MentorTimeSlotCreateRequest;

@Mapper(componentModel = "spring", uses = DateTimeMapper.class)
public interface MentorTimeSlotCreateRequestMapper {
    MentorTimeSlotCreateRequest toApi(ru.mentor.dto.MentorTimeSlotCreateRequest mentorTimeSlotCreateRequest);
    ru.mentor.dto.MentorTimeSlotCreateRequest toCommon(MentorTimeSlotCreateRequest apiMentorTimeSlotCreateRequest);
}
