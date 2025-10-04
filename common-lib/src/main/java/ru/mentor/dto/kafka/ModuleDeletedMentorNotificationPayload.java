package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи данных уведомления наставнику об удалении модуля.
 * Используется для формирования сообщений в Kafka при удалении модуля.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDeletedMentorNotificationPayload implements NotificationPayload {
    /**
     * Название курса.
     */
    private String courseTitle;

    /**
     * Название модуля.
     */
    private String moduleTitle;
}