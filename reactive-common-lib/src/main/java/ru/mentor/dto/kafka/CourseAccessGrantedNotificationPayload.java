package ru.mentor.dto.kafka;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.mentor.dto.UserInfoDto;

/**
 * DTO для передачи данных уведомления о предоставлении доступа к курсу.
 * Используется для формирования сообщений в Kafka при предоставлении доступа к курсу пользователю.
 */
@Data
@Builder
public class CourseAccessGrantedNotificationPayload implements NotificationPayload {

    /**
     * Название курса, к которому предоставлен доступ.
     */
    private String courseTitle;

    /**
     * Дата и время предоставления доступа к курсу.
     */
    private LocalDateTime accessGrantedAt;

    /**
     * Информация о пользователе, который предоставил доступ к курсу.
     */
    private UserInfoDto accessGrantedBy;

}
