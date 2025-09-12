package ru.mentor.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.CourseAccessRevokedNotificationPayload;
import ru.mentor.dto.kafka.CourseCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.CourseDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.ModuleAccessRevokedNotificationPayload;
import ru.mentor.dto.kafka.ModuleCreatedMentorNotificationPayload;
import ru.mentor.dto.kafka.UserRegistrationNotificationPayload;
import ru.mentor.dto.kafka.ModuleDeletedMentorNotificationPayload;
import ru.mentor.dto.kafka.SlotBookedNotificationPayload;

/**
 * Конфигурация для Jackson.
 * Нужна для корректной сериализации и десериализации объектов уведомлений.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Module notificationPayloadSubtypes() {
        SimpleModule module = new SimpleModule();

        module.registerSubtypes(
                CourseAccessGrantedNotificationPayload.class,
                CourseAccessRevokedNotificationPayload.class,
                CourseCreatedMentorNotificationPayload.class,
                CourseDeletedMentorNotificationPayload.class,
                ModuleAccessGrantedNotificationPayload.class,
                ModuleAccessRevokedNotificationPayload.class,
                ModuleCreatedMentorNotificationPayload.class,
                UserRegistrationNotificationPayload.class,
                ModuleDeletedMentorNotificationPayload.class,
                SlotBookedNotificationPayload.class
        );
        return module;
    }
}
