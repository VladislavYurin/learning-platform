package ru.mentor.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.constant.Role;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CalendarServiceGrpcClient;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.services.impl.RedirectCalendarServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectCalendarServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private CalendarServiceGrpcClient calendarServiceClient;

    @Mock
    private HeaderFactory headerFactory;

    @Spy
    private TimeSlotMapper timeSlotMapper = new TimeSlotMapper();

    @InjectMocks
    private RedirectCalendarServiceImpl redirectCalendarService;

    @BeforeEach
    void setUp() {
        Mockito.when(headerFactory.create(ArgumentMatchers.anyString()))
               .thenReturn(
                       Header.newBuilder()
                             .build()
               );
    }

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
                       ArgumentMatchers.any(Header.class),
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
                       ArgumentMatchers.any(Header.class),
                       ArgumentMatchers.anyLong(),
                       ArgumentMatchers.anyLong()
               );
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .grpcResponseToDto(testTimeSlotResponse);

    }

    @Test
    public void getMentorSlotsInfoForUser_passedMentorId_shouldUsePassedMentorId() {

        Mockito.when(userService.getCurrentUser())
               .thenReturn(TestDataGenerator.getTestParticipantUser());
        Mockito.when(calendarServiceClient.getMentorSlotsInfo(ArgumentMatchers.any()))
               .thenReturn(TestGrpcStubGenerator.constructMentorSlotsInfoResponse());

        redirectCalendarService.getMentorSlotsInfoForUser(TestConstantHolder.mentorId);

        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .toSlotInfoForUserList(ArgumentMatchers.any());

        // проверка вызовов во вспомогательном методе

        Mockito.verify(userService, Mockito.times(1))
               .getCurrentUser();
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .toMentorSlotsInfoGrpcRequest(
                       ArgumentMatchers.eq(TestConstantHolder.mentorId),
                       ArgumentMatchers.any(Header.class)
               );
        Mockito.verify(calendarServiceClient, Mockito.times(1))
               .getMentorSlotsInfo(ArgumentMatchers.any());
    }

    @Test
    public void getMentorSlotsInfoForMentor_noPassedMentorId_shouldUseCurrentUserId() {
        Mockito.when(userService.getCurrentUser())
               .thenReturn(TestDataGenerator.getTestMentorUser());
        Mockito.when(calendarServiceClient.getMentorSlotsInfo(ArgumentMatchers.any()))
               .thenReturn(TestGrpcStubGenerator.constructMentorSlotsInfoResponse());

        redirectCalendarService.getMentorSlotsInfoForMentor();

        Mockito.verify(calendarServiceClient, Mockito.times(1))
               .getMentorSlotsInfo(ArgumentMatchers.any());
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .toSlotInfoDtoList(ArgumentMatchers.any());

        Mockito.verify(userService, Mockito.times(1))
               .getCurrentUser();
        Mockito.verify(timeSlotMapper, Mockito.times(1))
               .toMentorSlotsInfoGrpcRequest(
                       ArgumentMatchers.eq(TestConstantHolder.mentorId),
                       ArgumentMatchers.any(Header.class)
               );
    }

}