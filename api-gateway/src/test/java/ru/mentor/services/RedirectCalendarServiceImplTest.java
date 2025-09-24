package ru.mentor.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.calendar.BookTimeSlotRequest;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.constant.Role;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.grpc.CalendarServiceGrpcClient;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.services.impl.RedirectCalendarServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectCalendarServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private CalendarServiceGrpcClient calendarServiceClient;

    @Spy
    private TimeSlotMapper timeSlotMapper = new TimeSlotMapper();

    @InjectMocks
    private RedirectCalendarServiceImpl redirectCalendarService;

    @Test
    public void createTimeSlot_Success() {

        MentorTimeSlotCreateRequest testCreateRequest = TestEntityStubGenerator.constructMentorTimeSlotCreateRequest();
        TimeSlotResponse testGrpcResponse = TestGrpcStubGenerator.constructTimeSlotResponse();
        UserEntity testUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);

        Mockito.when(userService.getCurrentUser()).thenReturn(testUser);
        Mockito.when(calendarServiceClient.createMentorTimeSlot(ArgumentMatchers.any(
                       CreateTimeSlotRequest.class)))
               .thenReturn(testGrpcResponse);

        MentorTimeSlotDto result = redirectCalendarService.createTimeSlot(testCreateRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestConstantHolder.timeSlotId, result.getId());
        Assertions.assertEquals(TestConstantHolder.mentorId, result.getMentorId());
        Assertions.assertEquals(testCreateRequest.getStartTime(), result.getStartTime());
        Assertions.assertEquals(testCreateRequest.getEndTime(), result.getEndTime());
        Assertions.assertEquals(TestConstantHolder.slotType, result.getSlotType());
        Assertions.assertEquals(TestConstantHolder.slotMeetingType, result.getSlotMeetingType());
        Assertions.assertEquals(TestConstantHolder.maxParticipants, result.getMaxParticipants());
        Assertions.assertEquals(testCreateRequest.getMeetingLink(), result.getMeetingLink());
        Assertions.assertEquals(testCreateRequest.getDescription(), result.getDescription());
        Assertions.assertNotNull(result.getCreatedAt());

        Mockito.verify(userService, Mockito.times(1)).getCurrentUser();
        Mockito.verify(calendarServiceClient, Mockito.times(1))
               .createMentorTimeSlot(ArgumentMatchers.any(CreateTimeSlotRequest.class));
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .requestCreateToGrpcDto(
                       ArgumentMatchers.any(),
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.any()
               );
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .grpcResponseToDto(ArgumentMatchers.any());
    }

    @Test
    public void bookTimeSlot_Success() {

        UserEntity testUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        Mockito.when(userService.getCurrentUser())
               .thenReturn(testUser);

        TimeSlotResponse testTimeSlotResponse = TestGrpcStubGenerator.constructTimeSlotResponse();
        Mockito.when(calendarServiceClient.bookTimeSlot(ArgumentMatchers.any(BookTimeSlotRequest.class)))
               .thenReturn(testTimeSlotResponse);

        MentorTimeSlotDto result = redirectCalendarService.bookTimeSlot(TestConstantHolder.timeSlotId);

        Assertions.assertNotNull(result);

        Mockito.verify(userService).getCurrentUser();
        Mockito.verify(calendarServiceClient, Mockito.times(1))
               .bookTimeSlot(ArgumentMatchers.any(BookTimeSlotRequest.class));
        Mockito.verify(timeSlotMapper).grpcResponseToDto(testTimeSlotResponse);
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .toGrpcBookTimeSlotRequest(
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.anyLong(),
                       ArgumentMatchers.anyLong()
               );
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .grpcResponseToDto(testTimeSlotResponse);

    }

}