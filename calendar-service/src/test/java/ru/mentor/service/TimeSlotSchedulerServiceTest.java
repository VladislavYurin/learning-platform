package ru.mentor.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.config.ReminderProperties;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.testUtil.TestDataGenerator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TimeSlotSchedulerServiceTest {

    private static MockedStatic<LocalDateTime> mockedLocalDateTime;
    private static LocalDateTime fixedTime;

    @Mock
    private MentorTimeSlotRepository repository;

    @Mock
    private CalendarNotificationService calendarNotificationService;

    @InjectMocks
    private TimeSlotSchedulerService timeSlotSchedulerService;

    @Mock
    private ReminderProperties reminderProperties;

    @BeforeAll
    static void setTime() {
        fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class);
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedTime);
    }

    @BeforeEach
    void setup() {
        reminderProperties.setRemindBeforeMinutes(60);
        timeSlotSchedulerService =
                new TimeSlotSchedulerService(reminderProperties, repository, calendarNotificationService);
    }

    @AfterAll
    static void tearDown() {
        mockedLocalDateTime.close();
    }

    @Test
    void checkTimeSlotsAndSendNotifications_noUpcomingSlots_noInteractions() {
        Mockito.when(repository.findUpcomingTimeSlotsWithParticipants(fixedTime,
                        fixedTime.plusMinutes(reminderProperties.getRemindBeforeMinutes())))
                .thenReturn(Collections.emptyList());

        timeSlotSchedulerService.checkTimeSlotsAndSendNotifications();

        Mockito.verify(repository, Mockito.times(1))
                .findUpcomingTimeSlotsWithParticipants(fixedTime,
                        fixedTime.plusMinutes(reminderProperties.getRemindBeforeMinutes()));

        Mockito.verifyNoInteractions(calendarNotificationService);
    }

    @Test
    void checkTimeSlotsAndSendNotifications_oneUpcomingSlot_sendReminders() {
        UserEntity student1 = TestDataGenerator.getTestParticipantUser();
        UserEntity student2 = TestDataGenerator.getAnotherTestParticipantUser();

        MentorTimeSlotEntity timeSlot = TestDataGenerator.createTestSlot(
                10L, Set.of(student1, student2), true,
                fixedTime.plusMinutes(10),
                fixedTime.plusMinutes(reminderProperties.getRemindBeforeMinutes()));

        Mockito.when(repository.findUpcomingTimeSlotsWithParticipants(
                fixedTime, fixedTime.plusMinutes(reminderProperties.getRemindBeforeMinutes())))
                .thenReturn(Collections.singletonList(timeSlot));

        timeSlotSchedulerService.checkTimeSlotsAndSendNotifications();

        Mockito.verify(repository, Mockito.times(1))
                .findUpcomingTimeSlotsWithParticipants(fixedTime,
                        fixedTime.plusMinutes(reminderProperties.getRemindBeforeMinutes()));
        Mockito.verify(calendarNotificationService, Mockito.times(1))
                .sendStudentReminder(timeSlot, student1);
        Mockito.verify(calendarNotificationService, Mockito.times(1))
                .sendStudentReminder(timeSlot, student2);
        Mockito.verify(calendarNotificationService, Mockito.times(1))
                .sendMentorReminder(timeSlot);
    }

    @Test
    void checkTimeSlotsAndSendNotifications_twoUpcomingSlots_calendarNotificationServiceCall() {
        UserEntity student1 = TestDataGenerator.getTestParticipantUser();
        UserEntity student2 = TestDataGenerator.getAnotherTestParticipantUser();

        MentorTimeSlotEntity timeSlot1 = TestDataGenerator.createTestSlot(
                11L, Set.of(student1, student2), true, fixedTime.plusMinutes(20), fixedTime.plusMinutes(40));
        MentorTimeSlotEntity timeSlot2 = TestDataGenerator.createTestSlot(
                12L, Set.of(student1), true, fixedTime.plusMinutes(40), fixedTime.plusMinutes(60));

        Mockito.when(repository.findUpcomingTimeSlotsWithParticipants(fixedTime,
                        fixedTime.plusMinutes(reminderProperties.getRemindBeforeMinutes())))
                .thenReturn(List.of(timeSlot1, timeSlot2));

        timeSlotSchedulerService.checkTimeSlotsAndSendNotifications();

        Mockito.verify(calendarNotificationService,
                Mockito.times(1)).sendStudentReminder(timeSlot1, student1);
        Mockito.verify(calendarNotificationService,
                Mockito.times(1)).sendStudentReminder(timeSlot2, student1);
        Mockito.verify(calendarNotificationService,
                Mockito.times(1)).sendMentorReminder(timeSlot1);
        Mockito.verify(calendarNotificationService,
                Mockito.times(1)).sendMentorReminder(timeSlot2);
        Mockito.verify(calendarNotificationService,
                Mockito.times(0)).sendStudentReminder(timeSlot2, student2);
    }

}