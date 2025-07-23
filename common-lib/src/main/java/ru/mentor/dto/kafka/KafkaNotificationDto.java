package ru.mentor.dto.kafka;

import lombok.Builder;
import lombok.Data;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;

@Data
@Builder
public class KafkaNotificationDto {

    private NotificationTypeEnum notificationType;

    private UserInfoDto userInfo;

    private NotificationPayload payload;

}
