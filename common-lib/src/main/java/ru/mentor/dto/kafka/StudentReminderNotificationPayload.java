package ru.mentor.dto.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных напоминания о предстоящей встрече.
 *
 */
@Data
@Builder
public class StudentReminderNotificationPayload implements NotificationPayload {
    /**
     * Имя ученика
     */
    private String studentName;
    /**
     * Время встречи
     */
    private LocalDateTime calendarSlotTime;
    /**
     * Имя ментора
     */
    private String mentorName;
    /**
     * Тип встречи
     */
    private String slotMeetingType;
    /**
     * Тип слота
     */
    private String slotType;
    /**
     * Описание
     */
    private String description;
    /**
     * Ссылка для встречи
     */
    private String meetingLink;
}
