package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.PageDetails;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;
import ru.mentor.entity.MentorTimeSlotEntity;
import java.util.List;

/**
 * Mapping helpers for mentor time slot entities and their gRPC models.
 */
@Component
public class TimeSlotMapper {

    public TimeSlotResponse entityToGrpcResponse(
            MentorTimeSlotEntity timeSlotEntity,
            String rqUId) {

        Timestamp startTime = UtilMapper.buildTimestamp(timeSlotEntity.getStartTime());
        Timestamp endTime = UtilMapper.buildTimestamp(timeSlotEntity.getEndTime());
        SlotType slotType = UtilMapper.calendarSlotTypeToSlotType(timeSlotEntity.getSlotType());
        SlotMeetingType slotMeetingType =
                UtilMapper.calendarSlotMeetingTypeToSlotMeetingType(timeSlotEntity.getSlotMeetingType());
        Timestamp createdAt = UtilMapper.buildTimestamp(timeSlotEntity.getCreatedAt());

        return TimeSlotResponse.newBuilder()
                               .setRequestId(rqUId)
                               .setSlotId(timeSlotEntity.getId())
                               .setMentorId(timeSlotEntity.getMentorId())
                               .setStartTime(startTime)
                               .setEndTime(endTime)
                               .setSlotType(slotType)
                               .setSlotMeetingType(slotMeetingType)
                               .setMaxParticipants(timeSlotEntity.getMaxParticipants())
                               .setMeetingLink(timeSlotEntity.getMeetingLink())
                               .setDescription(timeSlotEntity.getDescription())
                               .setCreatedAt(createdAt)
                               .build();
    }

    public MentorSlotInfo mentorTimeSlotEntityToMentorSlotInfo(
            TimeSlotResponse mentorTimeSlotInfo,
            List<UserInfo> timeSlotParticipants) {

        return MentorSlotInfo.newBuilder()
                             .setSlotInfo(mentorTimeSlotInfo)
                             .addAllParticipants(timeSlotParticipants)
                             .build();
    }

    public AllTimeSlotsResponse mapMentorTimeSlotEntityPageToAllTimeSlotsResponse(
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
