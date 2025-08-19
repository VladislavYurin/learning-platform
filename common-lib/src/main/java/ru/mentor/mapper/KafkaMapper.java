package ru.mentor.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.NotificationPayload;

/**
 * Маппер для создания DTO объектов, используемых при работе с Kafka.
 * Предоставляет методы для создания уведомлений и payloads для различных типов событий.
 */
@Component
public class KafkaMapper {

    /**
     * Создает DTO уведомления для отправки в Kafka.
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

}
