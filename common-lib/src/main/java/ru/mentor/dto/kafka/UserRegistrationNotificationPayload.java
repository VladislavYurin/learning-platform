package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных уведомления о регистрации пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationNotificationPayload implements NotificationPayload{

    /**
     * Дата и время регистрации пользователя
     */
    private LocalDateTime createdAt;
    /**
     * Информация о новом зарегистрированном пользователе
     */
    private UserInfoDto userInfo;
}
