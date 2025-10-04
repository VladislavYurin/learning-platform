package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных уведомления наставнику о создании курса.
 * Используется для формирования сообщений в Kafka при создании курса.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreatedMentorNotificationPayload implements NotificationPayload {

    /**
     * Название курса.
     */
    private String courseTitle;

    /**
     * Информация о создателе курса.
     */
    private UserInfoDto courseCreatedBy;

    /**
     * Дата и время создания курса.
     */
    private LocalDateTime createdAt;

}