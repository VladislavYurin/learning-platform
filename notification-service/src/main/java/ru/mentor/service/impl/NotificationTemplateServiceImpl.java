package ru.mentor.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.service.NotificationTemplateService;

/**
 * Реализация сервиса шаблонов уведомлений.
 *
 * Генерирует содержимое электронных писем и темы уведомлений
 * для различных типов уведомлений, используя предопределенные шаблоны.
 */
@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    /**
     * Предопределенные шаблоны электронных писем для различных типов уведомлений.
     */
    private final Map<NotificationTypeEnum, String> emailTemplates = Map.of(
            NotificationTypeEnum.COURSE_ACCESS_GRANTED, """
                    Уважаемый %s!
                    Вам предоставлен доступ к курсу "%s".
                    Доступ предоставил: %s %s
                    Дата предоставления: %s
                    """,
            NotificationTypeEnum.MODULE_ACCESS_GRANTED, """
                    Уважаемый %s!
                    Открыт новый модуль "%s" в курсе "%s".
                    Доступ предоставил: %s %s
                    Дата предоставления: %s
                    """
    );

    /**
     * Генерирует содержимое электронного письма на основе данных уведомления.
     *
     * @param dto объект, содержащий данные уведомления, для которого нужно сгенерировать содержание.
     * @return сгенерированное содержание электронного письма.
     * @throws IllegalArgumentException если тип уведомления неизвестен.
     */
    @Override
    public String generateEmailContent(KafkaNotificationDto dto) {
        String template = emailTemplates.get(dto.getNotificationType());

        if (template == null) {
            throw new IllegalArgumentException(
                    "Неизвестный тип уведомления: " + dto.getNotificationType());
        }

        return switch (dto.getNotificationType()) {
            case COURSE_ACCESS_GRANTED -> {
                CourseAccessGrantedNotificationPayload payload = (CourseAccessGrantedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getCourseTitle(),
                        payload.getAccessGrantedBy().getFirstName(),
                        payload.getAccessGrantedBy().getLastName(),
                        formatDateTime(payload.getAccessGrantedAt())
                );
            }
            case MODULE_ACCESS_GRANTED -> {
                ModuleAccessGrantedNotificationPayload payload = (ModuleAccessGrantedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getModuleTitle(),
                        payload.getCourseTitle(),
                        payload.getAccessGrantedBy().getFirstName(),
                        payload.getAccessGrantedBy().getLastName(),
                        formatDateTime(payload.getAccessGrantedAt())
                );
            }
        };
    }

    /**
     * Получает тему электронного письма на основе типа уведомления.
     * @param type тип уведомления, для которого требуется получить тему письма.
     * @return тема электронного письма для заданного типа уведомления.
     */
    @Override
    public String getEmailSubject(NotificationTypeEnum type) {
        return switch (type) {
            case COURSE_ACCESS_GRANTED -> "Доступ к курсу";
            case MODULE_ACCESS_GRANTED -> "Новый модуль доступен";
        };
    }

    /**
     * Форматирует дату и время в строку.
     *
     * @param dateTime дата и время для форматирования.
     * @return форматированная строка даты и времени.
     */
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

}
