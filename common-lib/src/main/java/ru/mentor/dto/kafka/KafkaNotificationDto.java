package ru.mentor.dto.kafka;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;

/**
 * DTO для передачи уведомлений через Kafka.
 * Представляет собой обертку для различных типов уведомлений, содержащую информацию о типе уведомления,
 * получателе и полезной нагрузке.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaNotificationDto {

    /**
     * Тип уведомления.
     * Определяет категорию уведомления.
     */
    private NotificationTypeEnum notificationType;

    /**
     * Информация о пользователе-получателе уведомления.
     * Содержит данные о пользователе, которому предназначено уведомление.
     */
    private UserInfoDto userInfo;

    /**
     * Полезная нагрузка уведомления.
     * Содержит данные, связанные с типом уведомления.
     */
    private NotificationPayload payload;

}
