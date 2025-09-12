package ru.mentor.service;

import ru.mentor.dto.kafka.KafkaNotificationDto;

/**
 * Интерфейс для сервиса уведомлений.
 */
public interface DefaultNotificationService {

    /**
     * Отправляет уведомление пользователю.
     *
     * @param notificationDto объект, содержащий данные уведомления, которое нужно отправить.
     */
    void notifyUser(KafkaNotificationDto notificationDto);

}
