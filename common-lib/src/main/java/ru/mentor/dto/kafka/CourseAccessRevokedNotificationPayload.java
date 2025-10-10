package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных уведомления об отзыве доступа к курсу.
 * Используется для формирования сообщений в Kafka при отзыве доступа к курсу у пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAccessRevokedNotificationPayload implements NotificationPayload {

    /**
     * Название курса, к которому отозван доступ.
     */
    private String courseTitle;

    /**
     * Дата и время отзыва доступа к курсу.
     */
    private LocalDateTime accessRevokedAt;

    /**
     * Информация о пользователе, который отозвал доступ к курсу.
     */
    private UserInfoDto accessRevokedBy;

}
