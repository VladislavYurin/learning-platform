package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;

import java.time.LocalDateTime;

/**
 * DTO для отправки уведомления об отзыве доступа к модулю.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAccessRevokedNotificationPayload implements NotificationPayload {

    /**
     * Название курса, к которому относится модуль.
     */
    private String courseTitle;

    /**
     * Название модуля, к которому отозван доступ.
     */
    private String moduleTitle;

    /**
     * Дата и время отзыва доступа к модулю.
     */
    private LocalDateTime accessRevokedAt;

    /**
     * Информация о пользователе, который отозвал доступ к модулю.
     */
    private UserInfoDto accessRevokedBy;

}
