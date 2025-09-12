package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных уведомления наставнику о создании модуля.
 * Используется для формирования сообщений в Kafka при создании модуля.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleCreatedMentorNotificationPayload implements NotificationPayload {
    /**
     * Название курса, к которому относится модуль.
     */
    private String courseTitle;

    /**
     * Название модуля.
     */
    private String moduleTitle;

    /**
     * Информация о создателе модуля.
     */
    private UserInfoDto moduleCreatedBy;

    /**
     * Получатель уведомления (наставник).
     */
    private UserInfoDto recipientUser;

    /**
     * Дата и время создания модуля.
     */
    private LocalDateTime createdAt;


}
