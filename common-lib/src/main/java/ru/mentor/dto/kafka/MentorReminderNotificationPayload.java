package ru.mentor.dto.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MentorReminderNotificationPayload implements NotificationPayload {
    /**
     * Имя ментора
     */
    private String mentorName;
    /**
     * Время встречи
     */
    private LocalDateTime calendarSlotTime;
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
    /**
     * Список учеников
     */
    private List<String> studentNames;
}
