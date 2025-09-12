package ru.mentor.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.CourseAccessRevokedNotificationPayload;
import ru.mentor.dto.kafka.CourseCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.CourseDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.ModuleAccessRevokedNotificationPayload;
import ru.mentor.dto.kafka.ModuleCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.NotificationPayload;
import ru.mentor.dto.kafka.UserRegistrationNotificationPayload;
import ru.mentor.dto.kafka.ModuleDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.SlotBookedNotificationPayload;


/**
 * Маппер для создания DTO объектов, используемых при работе с Kafka.
 * Предоставляет методы для создания уведомлений и payloads для различных типов событий.
 */
@Component
public class KafkaMapper {

    /**
     * Создает DTO уведомления для отправки в Kafka.
     *
     * @param notificationType тип уведомления
     * @param forUser информация о пользователе, для которого предназначено уведомление
     * @param notificationPayload содержимое уведомления
     * @return DTO уведомления для Kafka
     */
    public KafkaNotificationDto createKafkaNotificationDto(
            NotificationTypeEnum notificationType,
            UserInfoDto forUser,
            NotificationPayload notificationPayload) {
        return KafkaNotificationDto.builder()
                                   .notificationType(notificationType)
                                   .userInfo(forUser)
                                   .payload(notificationPayload)
                                   .build();
    }

    /**
     * Создает payload уведомления о предоставлении доступа к курсу.
     *
     * @param courseTitle название курса
     * @param accessGrantedAt дата и время предоставления доступа
     * @param accessGrantedBy информация о пользователе, предоставившем доступ
     * @return payload уведомления о доступе к курсу
     */
    public CourseAccessGrantedNotificationPayload createCourseAccessGrantedNotificationPayload(
            String courseTitle,
            LocalDateTime accessGrantedAt,
            UserInfoDto accessGrantedBy) {
        return CourseAccessGrantedNotificationPayload.builder()
                                                     .courseTitle(courseTitle)
                                                     .accessGrantedAt(accessGrantedAt)
                                                     .accessGrantedBy(accessGrantedBy)
                                                     .build();
    }

    /**
     * Создает payload уведомления о предоставлении доступа к модулю.
     *
     * @param courseTitle название курса
     * @param moduleTitle название модуля
     * @param accessGrantedAt дата и время предоставления доступа
     * @param accessGrantedBy информация о пользователе, предоставившем доступ
     * @return payload уведомления о доступе к модулю
     */
    public ModuleAccessGrantedNotificationPayload createModuleAccessGrantedNotificationPayload(
            String courseTitle,
            String moduleTitle,
            LocalDateTime accessGrantedAt,
            UserInfoDto accessGrantedBy) {
        return ModuleAccessGrantedNotificationPayload.builder()
                                                     .courseTitle(courseTitle)
                                                     .moduleTitle(moduleTitle)
                                                     .accessGrantedAt(accessGrantedAt)
                                                     .accessGrantedBy(accessGrantedBy)
                                                     .build();
    }

    /**
     * Создает payload уведомления о создании курса.
     *
     * @param courseTitle название курса
     * @param createdAt дата и время создания курса
     * @param createdBy создатель курса
     * @param recipientUser получатель уведомлления
     * @return payload уведомление о создании курса
     */
    public CourseCreatedMentorNotificationPayload courseCreatedMentorNotificationPayload(
            String courseTitle,
            LocalDateTime createdAt,
            UserInfoDto createdBy,
            UserInfoDto recipientUser) {
        return CourseCreatedMentorNotificationPayload.builder()
                .courseTitle(courseTitle)
                .courseCreatedBy(createdBy)
                .createdAt(createdAt)
                .recipientUser(recipientUser)
                .build();
    }

    /**
     * Создает payload уведомления о создании модуля в курсе.
     * @param courseTitle название курса, к которому относится модуль
     * @param moduleTitle название модуля
     * @param createdAt дата и время создания модуля
     * @param createdBy создатель модуля
     * @param recipientUser получатель уведомления
     * @return payload уведомление о создании модуля
     */
    public ModuleCreatedMentorNotificationPayload moduleCreatedMentorNotificationPayload(
            String courseTitle,
            String moduleTitle,
            LocalDateTime createdAt,
            UserInfoDto createdBy,
            UserInfoDto recipientUser) {
        return ModuleCreatedMentorNotificationPayload.builder()
                .courseTitle(courseTitle)
                .moduleTitle(moduleTitle)
                .moduleCreatedBy(createdBy)
                .createdAt(createdAt)
                .recipientUser(recipientUser)
                .build();
    }

    /**
     * Создает payload уведомления об удалении курса.
     * @param courseTitle название курса
     * @return payload уведомление об удалении курса
     */
    public CourseDeletedMentorNotificationPayload courseDeletedMentorNotificationPayload(
            String courseTitle) {
        return CourseDeletedMentorNotificationPayload.builder()
                .courseTitle(courseTitle)
                .build();
    }

    /**
     * Создает payload уведомления об отзыве доступа к курсу.
     *
     * @param courseTitle название курса
     * @param accessRevokedAt дата и время отзыва доступа
     * @param accessRevokedBy информация о пользователе, отозвавшем доступ
     * @return payload уведомления об отзыве доступа к курсу
     */
    public CourseAccessRevokedNotificationPayload createCourseAccessRevokedNotificationPayload(
            String courseTitle,
            LocalDateTime accessRevokedAt,
            UserInfoDto accessRevokedBy) {
        return CourseAccessRevokedNotificationPayload.builder()
                .courseTitle(courseTitle)
                .accessRevokedAt(accessRevokedAt)
                .accessRevokedBy(accessRevokedBy)
                .build();
    }

    /**
     * Создает payload уведомления об отзыве доступа к модулю.
     *
     * @param courseTitle название курса
     * @param moduleTitle название модуля
     * @param accessRevokedAt дата и время отзыва доступа
     * @param accessRevokedBy информация о пользователе, отозвавшем доступ
     * @return payload уведомления об отзыве доступа к модулю
     */
    public ModuleAccessRevokedNotificationPayload createModuleAccessRevokedNotificationPayload(
            String courseTitle,
            String moduleTitle,
            LocalDateTime accessRevokedAt,
            UserInfoDto accessRevokedBy) {
        return ModuleAccessRevokedNotificationPayload.builder()
                .courseTitle(courseTitle)
                .moduleTitle(moduleTitle)
                .accessRevokedAt(accessRevokedAt)
                .accessRevokedBy(accessRevokedBy)
                .build();
    }

    /**
     * Создает payload уведомления о регистрации пользователя.
     *
     * @param username имя пользователя
     * @param userInfo информация о пользователе
     * @return payload уведомления о регистрации
     */
    public UserRegistrationNotificationPayload userRegistrationNotificationPayload(
            String username,
            UserInfoDto userInfo) {
        return UserRegistrationNotificationPayload.builder()
                .userName(username)
                .createdAt(LocalDateTime.now())
                .userInfo(userInfo)
                .build();
    }


    /**
     * Создает payload уведомления об удалении модуля.
     * @param moduleTitle название модуля
     * @return payload уведомление об удалении модуля
     */
    public ModuleDeletedMentorNotificationPayload moduleDeletedMentorNotificationPayload(
            String moduleTitle) {
        return ModuleDeletedMentorNotificationPayload.builder()
                .moduleTitle(moduleTitle)
                .build();
    }

    /**
     * Создает payload уведомления наставнику о том, что слот забронирован.
     * @param mentor наставник
     * @param mentee ученик
     * @param startAt время начала встречи
     * @param endAt время окончания встречи
     * @return payload уведомления о забронированном слоте
     */
    public SlotBookedNotificationPayload slotBookedNotificationPayload(
            UserInfoDto mentor,
            UserInfoDto mentee,
            LocalDateTime startAt,
            LocalDateTime endAt){
        return SlotBookedNotificationPayload.builder()
                .mentor(mentor)
                .mentee(mentee)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
