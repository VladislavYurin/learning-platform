package ru.mentor.service;

import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.kafka.KafkaNotificationDto;

public interface NotificationTemplateService {

    String generateEmailContent(KafkaNotificationDto dto);

    String getEmailSubject(NotificationTypeEnum type);

}
