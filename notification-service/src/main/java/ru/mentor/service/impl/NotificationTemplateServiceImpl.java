package ru.mentor.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.cache.NotificationCacheProcessor;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.kafka.*;
import ru.mentor.service.NotificationTemplateService;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.UserRegistrationNotificationPayload;
import ru.mentor.dto.kafka.CourseDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.ModuleCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.CourseCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.ModuleDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.SlotBookedNotificationPayload;

/**
 * Реализация сервиса {@link ru.mentor.service.NotificationTemplateService} для работы с шаблонами уведомлений.
 * <p>
 * Сервис отвечает за:
 * <ul>
 *     <li>Генерацию содержимого email-сообщений на основе данных из {@link ru.mentor.dto.kafka.KafkaNotificationDto}
 *         и шаблонов, получаемых из {@link ru.mentor.cache.NotificationCacheProcessor}.</li>
 *     <li>Предоставление заголовков писем для разных типов уведомлений
 *         ({@link ru.mentor.constant.NotificationTypeEnum}).</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationCacheProcessor notificationCacheProcessor;

    /**
     * Формирует содержимое email-сообщения на основе данных из Kafka-уведомления и шаблона из кэша.
     * <p>
     * В зависимости от типа уведомления ({@link NotificationTypeEnum}):
     * <ul>
     *     <li>{@link NotificationTypeEnum#COURSE_ACCESS_GRANTED} — генерируется письмо о предоставлении доступа к курсу.</li>
     *     <li>{@link NotificationTypeEnum#MODULE_ACCESS_GRANTED} — генерируется письмо о предоставлении доступа к модулю.</li>
     *     <li>{@link NotificationTypeEnum#COURSE_CREATED_MENTOR} — генерируется письмо о создании курса.</li>
     *     <li>{@link NotificationTypeEnum#MODULE_CREATED_MENTOR} — генерируется письмо о создании модуля.</li>
     *     <li>{@link NotificationTypeEnum#COURSE_DELETED} — генерируется письмо об удалении курса.</li>
     *     <li>{@link NotificationTypeEnum#USER_REGISTRATION_USER} — генерируется письмо юзеру о регистрации на курсе.</li>
     *     <li>{@link NotificationTypeEnum#MODULE_DELETED} — генерируется письмо об удалении модуля.</li>
     *     <li>{@link NotificationTypeEnum#SLOT_BOOKED_MENTOR} — генерируется письмо о бронировании временного слота.</li>
     * </ul>
     * </p>
     *
     * @param dto объект {@link KafkaNotificationDto}, содержащий тип уведомления,
     * данные пользователя и payload с деталями события
     * @return готовое текстовое содержимое email-сообщения
     * @throws IllegalArgumentException если для указанного типа уведомления не найден шаблон
     */
    @Override
    public String generateEmailContent(KafkaNotificationDto dto) {
        String template = notificationCacheProcessor.getTemplateCache(dto.getNotificationType());

        if (template.isEmpty()) {
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
            case COURSE_CREATED_MENTOR -> {
                CourseCreatedMentorNotificationPayload payload = (CourseCreatedMentorNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getCourseTitle(),
                        payload.getCourseCreatedBy().getFirstName(),
                        payload.getCourseCreatedBy().getLastName(),
                        payload.getRecipientUser().getFirstName(),
                        payload.getRecipientUser().getLastName(),
                        formatDateTime(payload.getCreatedAt())
                );
            }
            case MODULE_CREATED_MENTOR -> {
                ModuleCreatedMentorNotificationPayload payload = (ModuleCreatedMentorNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getModuleTitle(),
                        payload.getCourseTitle(),
                        payload.getModuleCreatedBy().getFirstName(),
                        payload.getModuleCreatedBy().getLastName(),
                        payload.getRecipientUser().getFirstName(),
                        payload.getRecipientUser().getLastName(),
                        formatDateTime(payload.getCreatedAt())
                );
            }
            case COURSE_DELETED -> {
                CourseDeletedMentorNotificationPayload payload = (CourseDeletedMentorNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getCourseTitle()
                );
            }
            case USER_REGISTRATION_USER -> {
                UserRegistrationNotificationPayload payload = (UserRegistrationNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        formatDateTime(payload.getCreatedAt())
                );
            }
            case COURSE_ACCESS_REVOKED -> {
                CourseAccessRevokedNotificationPayload payload = (CourseAccessRevokedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getCourseTitle(),
                        payload.getAccessRevokedBy().getFirstName(),
                        payload.getAccessRevokedBy().getLastName(),
                        formatDateTime(payload.getAccessRevokedAt())
                );
            }
            case MODULE_ACCESS_REVOKED -> {
                ModuleAccessRevokedNotificationPayload payload = (ModuleAccessRevokedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getModuleTitle(),
                        payload.getCourseTitle(),
                        payload.getAccessRevokedBy().getFirstName(),
                        payload.getAccessRevokedBy().getLastName(),
                        formatDateTime(payload.getAccessRevokedAt())
                );
            }
            case MODULE_DELETED -> {
                ModuleDeletedMentorNotificationPayload payload = (ModuleDeletedMentorNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getModuleTitle()
                );
            }
            case SLOT_BOOKED_MENTOR -> {
                SlotBookedNotificationPayload payload = (SlotBookedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        formatDateTime(payload.getStartAt()),
                        formatDateTime(payload.getEndAt()),
                        payload.getMentee().getFirstName(),
                        payload.getMentee().getLastName()
                );
            }
        };
    }

    /**
     * Возвращает заголовок email-сообщения для указанного типа уведомления.
     *
     * @param type тип уведомления ({@link NotificationTypeEnum})
     * @return строка с темой письма
     */
    @Override
    public String getEmailSubject(NotificationTypeEnum type) {
        return switch (type) {
            case COURSE_ACCESS_GRANTED -> "Доступ к курсу";
            case MODULE_ACCESS_GRANTED -> "Новый модуль доступен";
            case COURSE_CREATED_MENTOR -> "Создан новый курс";
            case MODULE_CREATED_MENTOR -> "Создан новый модуль";
            case COURSE_DELETED -> "Удален курс";
            case USER_REGISTRATION_USER -> "Новый пользователь успешно зарегистрирован";
            case COURSE_ACCESS_REVOKED -> "Доступ к курсу отозван";
            case MODULE_ACCESS_REVOKED -> "Доступ к модулю отозван";
            case MODULE_DELETED -> "Модуль удален";
            case SLOT_BOOKED_MENTOR -> "Забронирован слот";
        };
    }

    /**
     * Форматирует дату и время в строку по шаблону {@code dd.MM.yyyy HH:mm}.
     *
     * @param dateTime объект {@link LocalDateTime}, подлежащий форматированию
     * @return строковое представление даты и времени
     */
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
