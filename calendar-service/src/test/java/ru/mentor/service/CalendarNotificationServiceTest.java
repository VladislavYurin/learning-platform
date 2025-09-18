package ru.mentor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.kafka.KafkaFacade;

@ExtendWith(MockitoExtension.class)
class CalendarNotificationServiceTest {

    @Mock
    private KafkaFacade mockKafkaFacade;

    @Mock
    private MentorTimeSlotEntity mockSlot;

    @Mock
    private UserEntity mockUser;

    @InjectMocks
    private CalendarNotificationService service;

    @Test
    void sendStudentReminder_shouldCallKafkaFacade() {
        service.sendStudentReminder(mockSlot, mockUser);
        Mockito.verify(mockKafkaFacade, Mockito.times(1))
                .sendStudentCalendarSlotReminderMessage(mockSlot, mockUser);
    }

    @Test
    void sendMentorReminder_shouldCallKafkaFacade() {
        service.sendMentorReminder(mockSlot);
        Mockito.verify(mockKafkaFacade, Mockito.times(1))
                .sendMentorCalendarSlotReminderMessage(mockSlot);
    }
}