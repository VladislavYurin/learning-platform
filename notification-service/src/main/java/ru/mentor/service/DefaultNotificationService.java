package ru.mentor.service;

import ru.mentor.dto.kafka.KafkaNotificationDto;

public interface DefaultNotificationService {

    void notifyUser(KafkaNotificationDto notificationDto);

}
