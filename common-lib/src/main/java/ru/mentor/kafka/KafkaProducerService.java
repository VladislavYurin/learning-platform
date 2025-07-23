package ru.mentor.kafka;

import ru.mentor.dto.kafka.KafkaNotificationDto;

public interface KafkaProducerService {

    void send(KafkaNotificationDto order);

}
