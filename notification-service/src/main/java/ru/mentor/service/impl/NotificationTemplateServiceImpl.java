package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.cache.NotificationCacheProcessor;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.CourseAccessRevokedNotificationPayload;
import ru.mentor.dto.kafka.CourseCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.CourseDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.MentorReminderNotificationPayload;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.ModuleAccessRevokedNotificationPayload;
import ru.mentor.dto.kafka.ModuleCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.ModuleDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.SlotBookedNotificationPayload;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.dto.kafka.UserRegistrationNotificationPayload;
import ru.mentor.service.NotificationTemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
     *     <li>{@link NotificationTypeEnum#MENTOR_CALENDAR_SLOT_REMINDER} — генерируется письмо c напоминанием о встрече для ментора.</li>
     *     <li>{@link NotificationTypeEnum#STUDENT_CALENDAR_SLOT_REMINDER} — генерируется письмо c напоминанием о встрече для ученика.</li>
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
            case COURSE_ACCESS_REVOKED -> {
                CourseAccessRevokedNotificationPayload payload = (CourseAccessRevokedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getCourseTitle(),
                        formatDateTime(payload.getAccessRevokedAt())
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
            case MODULE_ACCESS_REVOKED -> {
                ModuleAccessRevokedNotificationPayload payload = (ModuleAccessRevokedNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getModuleTitle(),
                        formatDateTime(payload.getAccessRevokedAt())
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
                        formatDateTime(payload.getCreatedAt())
                );
            }
            case MODULE_DELETED -> {
                ModuleDeletedMentorNotificationPayload payload = (ModuleDeletedMentorNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        payload.getCourseTitle(),
                        payload.getModuleTitle()
                );
            }
            case MENTOR_CALENDAR_SLOT_REMINDER -> {
                MentorReminderNotificationPayload payload = (MentorReminderNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        formatDateTime(payload.getCalendarSlotTime()),
                        payload.getSlotMeetingType(),
                        payload.getSlotType(),
                        payload.getDescription(),
                        payload.getMeetingLink(),
                        String.join(", ", payload.getStudentNames())
                );
            }
            case STUDENT_CALENDAR_SLOT_REMINDER -> {
                StudentReminderNotificationPayload payload = (StudentReminderNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        formatDateTime(payload.getCalendarSlotTime()),
                        payload.getMentorName(),
                        payload.getSlotMeetingType(),
                        payload.getSlotType(),
                        payload.getDescription(),
                        payload.getMeetingLink()
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
            case USER_REGISTRATION_USER -> {
                UserRegistrationNotificationPayload payload = (UserRegistrationNotificationPayload) dto.getPayload();
                yield String.format(
                        template,
                        dto.getUserInfo().getFirstName(),
                        formatDateTime(payload.getCreatedAt())
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
            case COURSE_ACCESS_REVOKED -> "Отзыв доступа к курсу";
            case COURSE_CREATED_MENTOR -> "Создан новый курс";
            case COURSE_DELETED -> "Удален курс";
            case MODULE_ACCESS_GRANTED -> "Новый модуль доступен";
            case MODULE_ACCESS_REVOKED -> "Отзыв доступа к модулю";
            case MODULE_CREATED_MENTOR -> "Создан новый модуль";
            case MODULE_DELETED -> "Модуль удален";
            case MENTOR_CALENDAR_SLOT_REMINDER, STUDENT_CALENDAR_SLOT_REMINDER -> "Напоминание о встрече";
            case SLOT_BOOKED_MENTOR -> "Забронирован слот";
            case USER_REGISTRATION_USER -> "Новый пользователь успешно зарегистрирован";
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
