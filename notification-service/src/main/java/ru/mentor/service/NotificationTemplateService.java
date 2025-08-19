package ru.mentor.service;

import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.kafka.KafkaNotificationDto;

/**
 * Интерфейс для сервиса шаблонов уведомлений.
 */
public interface NotificationTemplateService {

    /**
     * Генерирует содержимое электронного письма на основе данных уведомления.
     *
     * @param dto объект, содержащий данные уведомления для генерации содержимого письма.
     * @return сгенерированное содержимое электронного письма.
     */
    String generateEmailContent(KafkaNotificationDto dto);


    /**
     * Получает тему электронного письма на основе типа уведомления.
     *
     * @param type тип уведомления, для которого требуется получить тему письма.
     * @return тема электронного письма для заданного типа уведомления.
     */
    String getEmailSubject(NotificationTypeEnum type);

}
