package ru.mentor.dto.kafka;

import lombok.Builder;
import lombok.Data;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;

/**
 * DTO для передачи уведомлений через Kafka.
 * Представляет собой обертку для различных типов уведомлений, содержащую информацию о типе уведомления,
 * получателе и полезной нагрузке.
 */
@Data
@Builder
public class KafkaNotificationDto {

    /**
     * Тип уведомления.
     */
    private NotificationTypeEnum notificationType;

    /**
     * Информация о пользователе-получателе уведомления.
     */
    private UserInfoDto userInfo;

    /**
     * Полезная нагрузка уведомления.
     */
    private NotificationPayload payload;

}
