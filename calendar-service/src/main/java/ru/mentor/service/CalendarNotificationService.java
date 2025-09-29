package ru.mentor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.kafka.KafkaFacade;

@Service
@RequiredArgsConstructor
public class CalendarNotificationService {
    private final KafkaFacade kafkaFacade;

    /**
     * Отправить напоминание студенту для конкретного слота.
     */
    public void sendStudentReminder(MentorTimeSlotEntity slot, UserEntity student) {
        kafkaFacade.sendStudentCalendarSlotReminderMessage(slot, student);
    }

    /**
     * Отправить напоминание ментору для конкретного слота.
     */
    public void sendMentorReminder(MentorTimeSlotEntity slot) {
        kafkaFacade.sendMentorCalendarSlotReminderMessage(slot);
    }
}

