package ru.mentor.mapper;

import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.PageDetails;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;
import ru.mentor.entity.MentorTimeSlotEntity;

/**
 * Mapping helpers for mentor time slot entities and their gRPC models.
 */
@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimeSlotMapper {

    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "slotId", source = "timeSlotEntity.id")
    @Mapping(target = "mentorId", source = "timeSlotEntity.mentorId")
    @Mapping(target = "startTime", source = "timeSlotEntity.startTime",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "endTime", source = "timeSlotEntity.endTime",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "slotType", source = "timeSlotEntity.slotType",
            qualifiedByName = "calendarSlotTypeToSlotType")
    @Mapping(target = "slotMeetingType", source = "timeSlotEntity.slotMeetingType",
            qualifiedByName = "calendarSlotMeetingTypeToSlotMeetingType")
    @Mapping(target = "maxParticipants", source = "timeSlotEntity.maxParticipants")
    @Mapping(target = "meetingLink", source = "timeSlotEntity.meetingLink")
    @Mapping(target = "description", source = "timeSlotEntity.description")
    @Mapping(target = "createdAt", source = "timeSlotEntity.createdAt",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "isActive", source = "timeSlotEntity.isActive")
    TimeSlotResponse entityToGrpcResponse(
            MentorTimeSlotEntity timeSlotEntity,
            String requestId);

    @Mapping(target = "slotInfo", source = "mentorTimeSlotInfo")
    @Mapping(target = "participants", source = "timeSlotParticipants")
    MentorSlotInfo mentorTimeSlotEntityToMentorSlotInfo(
            TimeSlotResponse mentorTimeSlotInfo,
            List<UserInfo> timeSlotParticipants);

    default AllTimeSlotsResponse mapMentorTimeSlotEntityPageToAllTimeSlotsResponse(
            Page<MentorSlotInfo> mentorTimeSlotEntityPage) {

        return AllTimeSlotsResponse.newBuilder()
                                   .setPageDetails(
                                           extractPageDetailsFromMentorTimeSlotEntityPage(
                                                   mentorTimeSlotEntityPage))
                                   .addAllTimeSlots(mapMentorTimeSlotEntityPageToMentorSlotInfoList(
                                           mentorTimeSlotEntityPage))
                                   .build();
    }

    private List<MentorSlotInfo> mapMentorTimeSlotEntityPageToMentorSlotInfoList(
            Page<MentorSlotInfo> mentorTimeSlotEntityPage) {

        return mentorTimeSlotEntityPage.stream().toList();
    }

    private PageDetails extractPageDetailsFromMentorTimeSlotEntityPage(
            Page<MentorSlotInfo> mentorTimeSlotEntities) {

        return PageDetails.newBuilder()
                          .setPage(mentorTimeSlotEntities.getNumber())
                          .setSize(mentorTimeSlotEntities.getSize())
                          .setTotalPages(mentorTimeSlotEntities.getTotalPages())
                          .setTotalElements(mentorTimeSlotEntities.getTotalElements())
                          .build();
    }

}
