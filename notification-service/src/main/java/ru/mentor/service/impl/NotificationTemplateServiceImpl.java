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

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

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

    @Override
    public String getEmailSubject(NotificationTypeEnum type) {
        return switch (type) {
            case COURSE_ACCESS_GRANTED -> "Доступ к курсу";
            case MODULE_ACCESS_GRANTED -> "Новый модуль доступен";
        };
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

}
