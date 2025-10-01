package ru.mentor.kafka;

import ru.mentor.dto.kafka.KafkaNotificationDto;

/**
 * Интерфейс сервиса для отправки сообщений в Kafka.
 * Предоставляет метод для отправки уведомлений в формате KafkaNotificationDto.
 */
public interface KafkaProducerService {

    /**
     * Отправляет сообщение уведомления в Kafka.
     *
     * @param order DTO объект уведомления для отправки
     */
    void send(KafkaNotificationDto order);

}
