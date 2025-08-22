package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.calendar.*;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.Role;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TimeSlotMapperTest {

    TimeSlotMapper timeSlotMapper = new TimeSlotMapper();

    String testUUID = UUID.randomUUID().toString();
    String testLink = "link";
    String testDescription = "text";
    int testMaxParticipants = 10;

    String testUsername = "mentor";
    String testPassword = "password";
    String testFirstName = "John";
    String testLastName = "Doe";
    String testTgNickname = "@johndoe";

    public UserEntity getTestMentorUser() {
        return UserEntity.builder()
                .id(1L)
                .username(testUsername)
                .password(testPassword)
                .role(Role.MENTOR)
                .firstName(testFirstName)
                .lastName(testLastName)
                .tgNickname(testTgNickname)
                .build();
    }

    @Test
    void grpcResponseToDto() {
        Timestamp startTime = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.of(2025, 1, 15, 13, 0)
                        .toEpochSecond(ZoneOffset.UTC)).build();
        Timestamp endTime = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.of(2025, 1, 15, 14, 0)
                        .toEpochSecond(ZoneOffset.UTC)).build();
        Timestamp createdAt = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.of(2025, 1, 14, 10, 0)
                        .toEpochSecond(ZoneOffset.UTC)).build();

        TimeSlotResponse grpcResponse = TimeSlotResponse.newBuilder()
                .setRqUid(testUUID)
                .setSlotId(1L)
                .setMentorId(1L)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(testMaxParticipants)
                .setMeetingLink(testLink)
                .setDescription(testDescription)
                .setCreatedAt(createdAt)
                .build();

        MentorTimeSlotDto result = timeSlotMapper.grpcResponseToDto(grpcResponse);

        assertNotNull(result);
        assertEquals(testUUID, result.getRqUId());
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getMentorId());
        assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0), result.getStartTime());
        assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0), result.getEndTime());
        assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        assertEquals(testMaxParticipants, result.getMaxParticipants());
        assertEquals(testLink, result.getMeetingLink());
        assertEquals(testDescription, result.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 14, 10, 0), result.getCreatedAt());
    }

    @Test
    void requestCreateToGrpcDto() {
        MentorTimeSlotCreateRequest createRequest = MentorTimeSlotCreateRequest.builder()
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(testMaxParticipants)
                .meetingLink(testLink)
                .description(testDescription)
                .build();

        UserEntity testMentorUser = getTestMentorUser();
        CreateTimeSlotRequest result = timeSlotMapper.requestCreateToGrpcDto(createRequest, testUUID, testMentorUser);

        assertNotNull(result);
        assertEquals(testUUID, result.getRqUid());
        assertEquals(1L, result.getMentorId());
        assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0)
                        .toEpochSecond(ZoneOffset.UTC),
                result.getStartTime().getSeconds());
        assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0)
                        .toEpochSecond(ZoneOffset.UTC),
                result.getEndTime().getSeconds());
        assertEquals(SlotType.INDIVIDUAL, result.getSlotType());
        assertEquals(SlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        assertEquals(testMaxParticipants, result.getMaxParticipants());
        assertEquals(testLink, result.getMeetingLink());
        assertEquals(testDescription, result.getDescription());
    }

    @Test
    void grpcCreateRequestToEntity() {
        Timestamp startTime = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.of(2025, 1, 15, 13, 0)
                        .toEpochSecond(ZoneOffset.UTC)).build();
        Timestamp endTime = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.of(2025, 1, 15, 14, 0)
                        .toEpochSecond(ZoneOffset.UTC)).build();

        CreateTimeSlotRequest request = CreateTimeSlotRequest.newBuilder()
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(testMaxParticipants)
                .setMeetingLink(testLink)
                .setDescription(testDescription)
                .build();

        UserEntity testMentorUser = getTestMentorUser();
        MentorTimeSlotEntity result = timeSlotMapper.grpcCreateRequestToEntity(request, testMentorUser);

        assertNotNull(result);
        assertEquals(testMentorUser, result.getMentor());
        assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0), result.getStartTime());
        assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0), result.getEndTime());
        assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        assertEquals(testMaxParticipants, result.getMaxParticipants());
        assertEquals(testLink, result.getMeetingLink());
        assertEquals(testDescription, result.getDescription());
        assertTrue(result.getIsActive());
    }

    @Test
    void entityToGrpcResponse() {
        UserEntity testMentorUser = getTestMentorUser();
        MentorTimeSlotEntity timeSlotEntity = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(testMentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(testMaxParticipants)
                .meetingLink(testLink)
                .description(testDescription)
                .isActive(true)
                .createdAt(LocalDateTime.of(2025, 1, 14, 10, 0))
                .build();

        TimeSlotResponse result = timeSlotMapper.entityToGrpcResponse(timeSlotEntity, testUUID);

        assertNotNull(result);
        assertEquals(testUUID, result.getRqUid());
        assertEquals(1L, result.getSlotId());
        assertEquals(1L, result.getMentorId());
        assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0)
                        .toEpochSecond(ZoneOffset.UTC), result.getStartTime().getSeconds());
        assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0)
                        .toEpochSecond(ZoneOffset.UTC), result.getEndTime().getSeconds());
        assertEquals(SlotType.INDIVIDUAL, result.getSlotType());
        assertEquals(SlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        assertEquals(testMaxParticipants, result.getMaxParticipants());
        assertEquals(testLink, result.getMeetingLink());
        assertEquals(testDescription, result.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 14, 10, 0)
                        .toEpochSecond(ZoneOffset.UTC), result.getCreatedAt().getSeconds());
    }

}