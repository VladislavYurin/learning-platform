package ru.mentor.services;

import com.google.protobuf.Timestamp;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.calendar.*;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.Role;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.grpc.CalendarServiceGrpcClient;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.util.RqGenerator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class RedirectCalendarServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private CalendarServiceGrpcClient calendarServiceClient;

    @Spy
    private TimeSlotMapper timeSlotMapper = new TimeSlotMapper();

    @InjectMocks
    private ru.mentor.services.RedirectCalendarServiceImpl redirectCalendarService;

    String testLink = "link";
    String testDescription = "text";

    String testUsername = "user";
    String testPassword = "password";
    String testFirstName = "John";
    String testLastName = "Doe";
    String testTgNickname = "@johndoe";

    public UserEntity getTestUserWith(Role role) {
        return UserEntity.builder()
                .id(1L)
                .username(testUsername)
                .password(testPassword)
                .role(role)
                .firstName(testFirstName)
                .lastName(testLastName)
                .tgNickname(testTgNickname)
                .build();
    }

    private @NotNull TimeSlotResponse getTestTimeSlotResponse(LocalDateTime startTime, LocalDateTime endTime) {
        Timestamp startTimestamp = Timestamp.newBuilder()
                .setSeconds(startTime.toEpochSecond(ZoneOffset.UTC))
                .build();

        Timestamp endTimestamp = Timestamp.newBuilder()
                .setSeconds(endTime.toEpochSecond(ZoneOffset.UTC))
                .build();

        Timestamp createdAt = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .build();

        return TimeSlotResponse.newBuilder()
                .setRqUid(RqGenerator.generateRqId())
                .setSlotId(1L)
                .setMentorId(1L)
                .setStartTime(startTimestamp)
                .setEndTime(endTimestamp)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(5)
                .setMeetingLink(testLink)
                .setDescription(testDescription)
                .setCreatedAt(createdAt)
                .build();
    }

    private MentorTimeSlotCreateRequest getTestMentorTimeSlotCreateRequest() {
        return MentorTimeSlotCreateRequest.builder()
                .startTime(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .endTime(LocalDateTime.of(2024, 1, 1, 11, 0, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(5)
                .meetingLink(testLink)
                .description(testDescription)
                .build();
    }

    private MentorTimeSlotDto getTestMentorTimeSlotDto() {
        LocalDateTime now = LocalDateTime.now();
        return MentorTimeSlotDto.builder()
                .id(1L)
                .mentorId(123L)
                .rqUId("6e8f4e02-c91c-465f-b22d-7f102fca381b")
                .startTime(now)
                .endTime(now.plusHours(1))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(10)
                .isActive(true)
                .meetingLink("https://www.meet.ru/abc123-def456")
                .description("Знакомство и обсуждение плана дальнейшего взаимодействия")
                .createdAt(now.minusDays(1))
                .build();
    }

    @Test
    public void createTimeSlot_Success() {

        // Given
        MentorTimeSlotCreateRequest testCreateRequest = getTestMentorTimeSlotCreateRequest();
        TimeSlotResponse testGrpcResponse = getTestTimeSlotResponse(
                testCreateRequest.getStartTime(), testCreateRequest.getEndTime());
        UserEntity testUser = getTestUserWith(Role.MENTOR);

        Mockito.when(userService.getCurrentUser()).thenReturn(testUser);
        Mockito.when(calendarServiceClient.createMentorTimeSlot(any(CreateTimeSlotRequest.class)))
                .thenReturn(testGrpcResponse);

        // When
        MentorTimeSlotDto result = redirectCalendarService.createTimeSlot(testCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getMentorId());
        assertEquals(testCreateRequest.getStartTime(), result.getStartTime());
        assertEquals(testCreateRequest.getEndTime(), result.getEndTime());
        assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        assertEquals(5, result.getMaxParticipants());
        assertEquals(testCreateRequest.getMeetingLink(), result.getMeetingLink());
        assertEquals(testCreateRequest.getDescription(), result.getDescription());
        assertNotNull(result.getCreatedAt());

        Mockito.verify(userService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(calendarServiceClient, Mockito.times(1))
                .createMentorTimeSlot(any(CreateTimeSlotRequest.class));
        Mockito.verify(timeSlotMapper, Mockito.times(1))
                .requestCreateToGrpcDto(any(), anyString(), any());
        Mockito.verify(timeSlotMapper, Mockito.times(1))
                .grpcResponseToDto(any());
    }

    @Test
    public void bookTimeSlot_Success() {

        // Given
        UserEntity testUser = getTestUserWith(Role.USER);
        Mockito.when(userService.getCurrentUser())
                .thenReturn(testUser);

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1L);
        TimeSlotResponse testTimeSlotResponse = getTestTimeSlotResponse(startTime, endTime);
        Mockito.when(calendarServiceClient.bookTimeSlot(any(BookTimeSlotRequest.class)))
                        .thenReturn(testTimeSlotResponse);

        // When
        long slotId = 1L;
        MentorTimeSlotDto result = redirectCalendarService.bookTimeSlot(slotId);

        // Then
        assertNotNull(result);

        Mockito.verify(userService).getCurrentUser();
        Mockito.verify(calendarServiceClient, Mockito.times(1))
                .bookTimeSlot(any(BookTimeSlotRequest.class));
        Mockito.verify(timeSlotMapper).grpcResponseToDto(testTimeSlotResponse);
        Mockito.verify(timeSlotMapper, Mockito.times(1))
                .toGrpcBookTimeSlotRequest(anyString(), anyLong(), anyLong());
        Mockito.verify(timeSlotMapper, Mockito.times(1))
                .grpcResponseToDto(testTimeSlotResponse);

    }
}