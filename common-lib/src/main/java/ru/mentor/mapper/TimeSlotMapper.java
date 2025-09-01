package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.mentor.calendar.*;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Маппер для {@link MentorTimeSlotEntity}
 */
@Component
public class TimeSlotMapper {

    /**
     * Преобразовывает объект, полученный через gRPC в DTO для отправки клиенту.
     *
     * @param grpcResponse {@link TimeSlotResponse}
     * @return {@link MentorTimeSlotDto}
     */
    public MentorTimeSlotDto grpcResponseToDto(TimeSlotResponse grpcResponse) {

        Timestamp grpcStartTime = grpcResponse.getStartTime();
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(
                grpcStartTime.getSeconds(), grpcStartTime.getNanos(), ZoneOffset.UTC);

        Timestamp grpcEndTime = grpcResponse.getEndTime();
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(
                grpcEndTime.getSeconds(), grpcEndTime.getNanos(), ZoneOffset.UTC);

        CalendarSlotType slotType = CalendarSlotType
                .valueOf(grpcResponse.getSlotType().toString());
        CalendarSlotMeetingType slotMeetingType = CalendarSlotMeetingType
                .valueOf(grpcResponse.getSlotMeetingType().toString());

        Timestamp grpcCreatedAt = grpcResponse.getCreatedAt();
        LocalDateTime createdAt = LocalDateTime.ofEpochSecond(
                grpcCreatedAt.getSeconds(), grpcCreatedAt.getNanos(), ZoneOffset.UTC);

        return MentorTimeSlotDto.builder()
                .rqUId(grpcResponse.getRqUid())
                .id(grpcResponse.getSlotId())
                .mentorId(grpcResponse.getMentorId())
                .startTime(startTime)
                .endTime(endTime)
                .slotType(slotType)
                .slotMeetingType(slotMeetingType)
                .maxParticipants(grpcResponse.getMaxParticipants())
                .meetingLink(grpcResponse.getMeetingLink())
                .description(grpcResponse.getDescription())
                .createdAt(createdAt)
                .build();
    }

    /**
     * Преобразовывает объект, полученный от клиента в DTO для отправки по gRPC.
     *
     * @param createRequest {@link MentorTimeSlotCreateRequest}
     * @param rqUId UUID запроса
     * @param user объект {@link UserEntity}
     * @return {@link CreateTimeSlotRequest}
     */
    public CreateTimeSlotRequest requestCreateToGrpcDto(MentorTimeSlotCreateRequest createRequest,
                                                        String rqUId, UserEntity user) {

        Timestamp startTime = Timestamp.newBuilder()
                .setSeconds(createRequest.getStartTime()
                        .toEpochSecond(ZoneOffset.UTC)).build();

        Timestamp endTime = Timestamp.newBuilder()
                .setSeconds(createRequest.getEndTime()
                        .toEpochSecond(ZoneOffset.UTC)).build();

        SlotType slotType = SlotType.valueOf(
                createRequest.getSlotType().toString());

        SlotMeetingType slotMeetingType = SlotMeetingType.valueOf(
                createRequest.getSlotMeetingType().toString());

        return CreateTimeSlotRequest.newBuilder()
                .setRqUid(rqUId)
                .setMentorId(user.getId())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setSlotType(slotType)
                .setSlotMeetingType(slotMeetingType)
                .setMaxParticipants(createRequest.getMaxParticipants())
                .setMeetingLink(createRequest.getMeetingLink())
                .setDescription(createRequest.getDescription())
                .build();
    }

    /**
     * Преобразовывает DTO, полученный по rRPC в доменную сущность.
     *
     * @param request {@link CreateTimeSlotRequest}
     * @param mentor {@link UserEntity}
     * @return {@link MentorTimeSlotEntity}
     */
    public MentorTimeSlotEntity grpcCreateRequestToEntity(CreateTimeSlotRequest request, UserEntity mentor) {

        Timestamp startTimestamp = request.getStartTime();
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(startTimestamp.getSeconds(),
                startTimestamp.getNanos(), ZoneOffset.UTC);

        Timestamp endTimestamp = request.getEndTime();
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(endTimestamp.getSeconds(),
                endTimestamp.getNanos(), ZoneOffset.UTC);

        CalendarSlotType slotType = CalendarSlotType.valueOf(
                request.getSlotType().toString());

        CalendarSlotMeetingType slotMeetingType = CalendarSlotMeetingType.valueOf(
                request.getSlotMeetingType().toString());

        return MentorTimeSlotEntity.builder()
                .mentor(mentor)
                .startTime(startTime)
                .endTime(endTime)
                .description(request.getDescription())
                .slotType(slotType)
                .slotMeetingType(slotMeetingType)
                .maxParticipants(request.getMaxParticipants())
                .meetingLink(request.getMeetingLink())
                .isActive(true)
                .build();
    }

    /**
     * Преобразовывает доменную сущность в DTO для ответа по gRPC.
     *
     * @param timeSlotEntity {@link MentorTimeSlotEntity}
     * @param rqUId UUID запроса
     * @return {@link TimeSlotResponse}
     */
    public TimeSlotResponse entityToGrpcResponse(MentorTimeSlotEntity timeSlotEntity, String rqUId) {

        Timestamp startTime = Timestamp.newBuilder()
                .setSeconds(timeSlotEntity.getStartTime()
                        .toEpochSecond(ZoneOffset.UTC)).build();

        Timestamp endTime = Timestamp.newBuilder()
                .setSeconds(timeSlotEntity.getEndTime()
                        .toEpochSecond(ZoneOffset.UTC)).build();

        SlotType slotType = SlotType.valueOf(
                timeSlotEntity.getSlotType().toString());

        SlotMeetingType slotMeetingType = SlotMeetingType.valueOf(
                timeSlotEntity.getSlotMeetingType().toString());

        Timestamp createdAt = Timestamp.newBuilder()
                .setSeconds(timeSlotEntity.getCreatedAt()
                        .toEpochSecond(ZoneOffset.UTC)).build();

        return TimeSlotResponse.newBuilder()
                .setRqUid(rqUId)
                .setSlotId(timeSlotEntity.getId())
                .setMentorId(timeSlotEntity.getMentor().getId())
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

    /**
     * Маппит уникальный ID запроса, слота и пользователя в объект запроса на бронирование сбота для отправки по gRPC.
     *
     * @param rqUid UUID запроса на бронирование слота
     * @param slotId ID слота, который бронируется
     * @param userId ID пользователя, который бронирует
     * @return {@link BookTimeSlotRequest}
     */
    public BookTimeSlotRequest toGrpcBookTimeSlotRequest(String rqUid, long slotId, long userId) {
        return BookTimeSlotRequest.newBuilder()
                .setRqUid(rqUid)
                .setSlotId(slotId)
                .setUserId(userId)
                .build();
    }

}
