package ru.mentor.dto.kafka;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;

/**
 * DTO для передачи данных уведомления о предоставлении доступа к модулю.
 * Используется для формирования сообщений в Kafka при предоставлении доступа к модулю курса пользователю.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAccessGrantedNotificationPayload implements NotificationPayload {

    /**
     * Название курса, к которому относится модуль.
     */
    private String courseTitle;

    /**
     * Название модуля, к которому предоставлен доступ.
     */
    private String moduleTitle;

    /**
     * Дата и время предоставления доступа к модулю.
     */
    private LocalDateTime accessGrantedAt;

    /**
     * Информация о пользователе, который предоставил доступ к модулю.
     */
    private UserInfoDto accessGrantedBy;

}
