package ru.mentor.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import ru.mentor.constant.NotificationDestination;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.NotificationPayload;
import ru.mentor.entity.NotificationEntity;
import ru.mentor.entity.UserEntity;

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
     * Создает сущность уведомления из DTO.
     *
     * @param notificationDto DTO уведомление из Kafka
     * @param notificationDestination пункт назначения уведомления
     * @param exceptionMessage сообщение об ошибке
     * @param notificationStatus статус уведомления
     * @param userEntity сущность пользователя
     * @return сущность уведомления
     */
    public NotificationEntity mapNotificationEntity(
            KafkaNotificationDto notificationDto,
            NotificationDestination notificationDestination,
            String exceptionMessage,
            NotificationStatus notificationStatus,
            UserEntity userEntity) {
        return NotificationEntity.builder()
                                 .notificationType(notificationDto.getNotificationType())
                                 .recipient(userEntity)
                                 .notificationDestination(notificationDestination)
                                 .notificationStatus(notificationStatus)
                                 .build();
    }
}
