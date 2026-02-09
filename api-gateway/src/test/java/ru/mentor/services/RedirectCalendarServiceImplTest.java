package ru.mentor.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CalendarServiceGrpcClient;
import ru.mentor.mapper.TimeSlotMapperImpl;
import ru.mentor.mapper.UtilMapperImpl;
import ru.mentor.services.impl.RedirectCalendarServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        RedirectCalendarServiceImpl.class,
        TimeSlotMapperImpl.class,
        UtilMapperImpl.class
})
class RedirectCalendarServiceImplTest {

    @MockBean
    private CalendarServiceGrpcClient calendarServiceClient;

    @MockBean
    private UserService userService;

    @MockBean
    private HeaderFactory headerFactory;

    @Autowired
    private RedirectCalendarServiceImpl redirectCalendarService;

    @BeforeEach
    void setUp() {
        Mockito.when(headerFactory.create(ArgumentMatchers.anyString()))
                .thenReturn(TestGrpcStubGenerator.constructHeader());
    }

    @Test
    public void createTimeSlot_Success() {

        MentorTimeSlotCreateRequest testCreateRequest = TestEntityStubGenerator.constructMentorTimeSlotCreateRequest();
        TimeSlotResponse testGrpcResponse = TestGrpcStubGenerator.constructTimeSlotResponse();
        UserEntity testUser = TestDataGenerator.getMentorEntity();

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
    }

    @Test
    public void bookTimeSlot_Success() {

        UserEntity testUser = TestDataGenerator.getUserEntity();
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
    }

    @Test
    public void getMentorSlotsInfoForUser_passedMentorId_shouldUsePassedMentorId() {

        Mockito.when(userService.getCurrentUser())
                .thenReturn(TestDataGenerator.getUserEntity());
        Mockito.when(calendarServiceClient.getMentorSlotsInfo(ArgumentMatchers.any()))
                .thenReturn(TestGrpcStubGenerator.constructMentorSlotsInfoResponse());

        redirectCalendarService.getMentorSlotsInfoForUser(TestConstantHolder.mentorId);

        // проверка вызовов во вспомогательном методе
        Mockito.verify(userService, Mockito.times(1))
                .getCurrentUser();
        Mockito.verify(calendarServiceClient, Mockito.times(1))
                .getMentorSlotsInfo(ArgumentMatchers.any());
    }

    @Test
    public void getMentorSlotsInfoForMentor_noPassedMentorId_shouldUseCurrentUserId() {
        Mockito.when(userService.getCurrentUser())
                .thenReturn(TestDataGenerator.getMentorEntity());
        Mockito.when(calendarServiceClient.getMentorSlotsInfo(ArgumentMatchers.any()))
                .thenReturn(TestGrpcStubGenerator.constructMentorSlotsInfoResponse());

        redirectCalendarService.getMentorSlotsInfoForMentor();

        Mockito.verify(calendarServiceClient, Mockito.times(1))
                .getMentorSlotsInfo(ArgumentMatchers.any());
        Mockito.verify(userService, Mockito.times(1))
                .getCurrentUser();
    }

}