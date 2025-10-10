package ru.mentor.dto.kafka;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Маркерный интерфейс для полезной нагрузки уведомлений.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "notificationType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CourseAccessGrantedNotificationPayload.class, name = "COURSE_ACCESS_GRANTED"),
        @JsonSubTypes.Type(value = CourseAccessRevokedNotificationPayload.class, name = "COURSE_ACCESS_REVOKED"),
        @JsonSubTypes.Type(value = CourseCreatedMentorNotificationPayload.class, name = "COURSE_CREATED_MENTOR"),
        @JsonSubTypes.Type(value = CourseDeletedMentorNotificationPayload.class, name = "COURSE_DELETED"),
        @JsonSubTypes.Type(value = ModuleAccessGrantedNotificationPayload.class, name = "MODULE_ACCESS_GRANTED"),
        @JsonSubTypes.Type(value = ModuleAccessRevokedNotificationPayload.class, name = "MODULE_ACCESS_REVOKED"),
        @JsonSubTypes.Type(value = ModuleCreatedMentorNotificationPayload.class, name = "MODULE_CREATED_MENTOR"),
        @JsonSubTypes.Type(value = ModuleDeletedMentorNotificationPayload.class, name = "MODULE_DELETED"),
        @JsonSubTypes.Type(value = SlotBookedNotificationPayload.class, name = "SLOT_BOOKED_MENTOR"),
        @JsonSubTypes.Type(value = UserRegistrationNotificationPayload.class, name = "USER_REGISTRATION_USER")

})
public interface NotificationPayload {
}
