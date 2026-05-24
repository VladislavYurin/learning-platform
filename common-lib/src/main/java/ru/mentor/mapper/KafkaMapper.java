package ru.mentor.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.constant.NotificationDestination;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
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
import ru.mentor.dto.kafka.NotificationPayload;
import ru.mentor.dto.kafka.SlotBookedNotificationPayload;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.dto.kafka.UserRegistrationNotificationPayload;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.NotificationEntity;
import ru.mentor.entity.UserEntity;

/**
 * Маппер для создания DTO объектов, используемых при работе с Kafka.
 * Предоставляет методы для создания уведомлений и payloads для различных типов событий.
 */
@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KafkaMapper {

    /**
     * Создает DTO уведомления для отправки в Kafka.
     *
     * @param notificationType тип уведомления
     * @param forUser информация о пользователе, для которого предназначено уведомление
     * @param notificationPayload содержимое уведомления
     * @return DTO уведомления для Kafka
     */
    @Mapping(target = "userInfo", source = "forUser")
    @Mapping(target = "payload", source = "notificationPayload")
    KafkaNotificationDto createKafkaNotificationDto(
            NotificationTypeEnum notificationType,
            UserInfoDto forUser,
            NotificationPayload notificationPayload);

    /**
     * Создает payload уведомления о предоставлении доступа к курсу.
     *
     * @param courseTitle название курса
     * @param accessGrantedAt дата и время предоставления доступа
     * @param accessGrantedBy информация о пользователе, предоставившем доступ
     * @return payload уведомления о доступе к курсу
     */
    CourseAccessGrantedNotificationPayload createCourseAccessGrantedNotificationPayload(
            String courseTitle,
            LocalDateTime accessGrantedAt,
            UserInfoDto accessGrantedBy);

    /**
     * Создает payload уведомления об отзыве доступа к курсу.
     *
     * @param courseTitle название курса
     * @param accessRevokedAt дата и время отзыва доступа
     * @param accessRevokedBy информация о пользователе, отозвавшем доступ
     * @return payload уведомления об отзыве доступа к курсу
     */
    CourseAccessRevokedNotificationPayload createCourseAccessRevokedNotificationPayload(
            String courseTitle,
            LocalDateTime accessRevokedAt,
            UserInfoDto accessRevokedBy);

    /**
     * Создает payload уведомления о создании курса.
     *
     * @param courseTitle название курса
     * @param createdAt дата и время создания курса
     * @param createdBy создатель курса
     * @return payload уведомление о создании курса
     */
    @Mapping(target = "courseCreatedBy", source = "createdBy")
    CourseCreatedMentorNotificationPayload courseCreatedMentorNotificationPayload(
            String courseTitle,
            LocalDateTime createdAt,
            UserInfoDto createdBy);

    /**
     * Создает payload уведомления об удалении курса.
     * @param courseTitle название курса
     * @return payload уведомление об удалении курса
     */
    CourseDeletedMentorNotificationPayload courseDeletedMentorNotificationPayload(
            String courseTitle);

    /**
     * Создает payload уведомления о предоставлении доступа к модулю.
     *
     * @param courseTitle название курса
     * @param moduleTitle название модуля
     * @param accessGrantedAt дата и время предоставления доступа
     * @param accessGrantedBy информация о пользователе, предоставившем доступ
     * @return payload уведомления о доступе к модулю
     */
    ModuleAccessGrantedNotificationPayload createModuleAccessGrantedNotificationPayload(
            String courseTitle,
            String moduleTitle,
            LocalDateTime accessGrantedAt,
            UserInfoDto accessGrantedBy);

    /**
     * Создает payload уведомления об отзыве доступа к модулю.
     *
     * @param courseTitle название курса
     * @param moduleTitle название модуля
     * @param accessRevokedAt дата и время отзыва доступа
     * @param accessRevokedBy информация о пользователе, отозвавшем доступ
     * @return payload уведомления об отзыве доступа к модулю
     */
    ModuleAccessRevokedNotificationPayload createModuleAccessRevokedNotificationPayload(
            String courseTitle,
            String moduleTitle,
            LocalDateTime accessRevokedAt,
            UserInfoDto accessRevokedBy);

    /**
     * Создает payload уведомления о создании модуля в курсе.
     * @param courseTitle название курса, к которому относится модуль
     * @param moduleTitle название модуля
     * @param createdAt дата и время создания модуля
     * @param createdBy создатель модуля
     * @return payload уведомление о создании модуля
     */
    @Mapping(target = "moduleCreatedBy", source = "createdBy")
    ModuleCreatedMentorNotificationPayload moduleCreatedMentorNotificationPayload(
            String courseTitle,
            String moduleTitle,
            LocalDateTime createdAt,
            UserInfoDto createdBy);

    /**
     * Создает payload уведомления об удалении модуля.
     * @param courseTitle название курса
     * @param moduleTitle название модуля
     * @return payload уведомление об удалении модуля
     */
    ModuleDeletedMentorNotificationPayload moduleDeletedMentorNotificationPayload(
            String courseTitle,
            String moduleTitle);

    /**
     * Создает payload уведомления о регистрации пользователя.
     *
     * @param userInfo информация о пользователе
     * @return payload уведомления о регистрации
     */
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    UserRegistrationNotificationPayload userRegistrationNotificationPayload(
            UserInfoDto userInfo);

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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "notificationType", source = "notificationDto.notificationType")
    @Mapping(target = "recipient", source = "userEntity")
    @Mapping(target = "notificationStatus", source = "notificationStatus")
    @Mapping(target = "notificationDestination", source = "notificationDestination")
    @Mapping(target = "errorText", source = "exceptionMessage")
    NotificationEntity mapNotificationEntity(
            KafkaNotificationDto notificationDto,
            NotificationDestination notificationDestination,
            String exceptionMessage,
            NotificationStatus notificationStatus,
            UserEntity userEntity);

    /**
     * Создает payload уведомления наставнику о том, что слот забронирован.
     * @param startAt время начала встречи
     * @param endAt время окончания встречи
     * @param mentee ученик
     * @return payload уведомления о забронированном слоте
     */
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "mentee", source = "mentee")
    SlotBookedNotificationPayload slotBookedNotificationPayload(
            LocalDateTime startAt,
            LocalDateTime endAt,
            UserInfoDto mentee);

    /**
     * Создает payload уведомления для студента о предстоящей встрече
     * @param slot слот, о котором нужно напомнить
     * @param student студент, которому нужно направить уведомление
     * @return {@link StudentReminderNotificationPayload}
     */
    @Mapping(target = "studentName", source = "student.firstName")
    @Mapping(target = "calendarSlotTime", source = "slot.startTime")
    @Mapping(target = "mentorName", source = "slot.mentor.firstName")
    @Mapping(target = "slotMeetingType", source = "slot.slotMeetingType",
            qualifiedByName = "calendarSlotMeetingTypeToSlotMeetingType")
    @Mapping(target = "slotType", source = "slot.slotType",
            qualifiedByName = "calendarSlotTypeToSlotType")
    @Mapping(target = "description", source = "slot.description")
    @Mapping(target = "meetingLink", source = "slot.meetingLink")
    StudentReminderNotificationPayload createStudentReminderNotificationPayload(
            MentorTimeSlotEntity slot,
            UserEntity student);

    /**
     * Создает payload уведомления для ментора о предстоящей встрече
     * @param slot слот, о котором нужно напомнить
     *
     * @return {@link MentorReminderNotificationPayload}
     */
    @Mapping(target = "mentorName", source = "slot.mentor.firstName")
    @Mapping(target = "calendarSlotTime", source = "slot.startTime")
    @Mapping(target = "slotMeetingType", source = "slot.slotMeetingType",
            qualifiedByName = "calendarSlotMeetingTypeToSlotMeetingType")
    @Mapping(target = "slotType", source = "slot.slotType",
            qualifiedByName = "calendarSlotTypeToSlotType")
    @Mapping(target = "description", source = "slot.description")
    @Mapping(target = "meetingLink", source = "slot.meetingLink")
    @Mapping(target = "studentNames", source = "slot",
            qualifiedByName = "mapStudentNames")
    MentorReminderNotificationPayload createMentorReminderNotificationPayload(
            MentorTimeSlotEntity slot);

    @Named("mapStudentNames")
    default List<String> mapStudentNames (MentorTimeSlotEntity slot) {
        return slot.getMeetingParticipants().stream().map(UserEntity::getFirstName).toList();
    }

}
