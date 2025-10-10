package ru.mentor.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.dto.UserInfoDto;
import java.time.LocalDateTime;

/**
 * DTO для передачи данных уведомления наставнику о бронировании слота учеником.
 * Используется для формирования сообщений в Kafka при бронировании слота.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotBookedNotificationPayload implements NotificationPayload{

    /**
     * Дата и время начала встречи.
     */
    private LocalDateTime startAt;

    /**
     * Дата и время окончания встречи.
     */
    private LocalDateTime endAt;

    /**
     * Информация об ученике, который забронировал слот.
     */
    private UserInfoDto mentee;
}