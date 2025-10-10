package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи данных уведомления наставнику об удалении курса.
 * Используется для формирования сообщений в Kafka при удалении курса.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDeletedMentorNotificationPayload implements NotificationPayload {

    /**
     * Название курса.
     */
    private String courseTitle;

}