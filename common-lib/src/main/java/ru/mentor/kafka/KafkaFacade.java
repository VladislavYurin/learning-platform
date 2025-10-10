package ru.mentor.kafka;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.CourseCreatedMentorNotificationPayload;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserCourseAccessEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.entity.UserModuleAccessEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.KafkaMapper;

/**
 * Фасадный сервис для отправки сообщений в Kafka.
 * Обертка над KafkaTemplate для отправки сообщений.
 * Предоставляет удобные методы для отправки различных типов уведомлений,
 * связанных с доступом пользователей к курсам и модулям.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaFacade {

    /**
     * Сервис для отправки сообщений в Kafka.
     */
    private final KafkaProducerService kafkaProducerService;

    /**
     * Маппер для преобразования сущностей в DTO для Kafka сообщений.
     */
    private final KafkaMapper kafkaMapper;

    /**
     * Базовый маппер для преобразования сущностей в DTO.
     */
    private final BaseMapper baseMapper;

    /**
     * Отправляет сообщение о предоставлении доступа к курсу.
     * Создает и отправляет уведомление типа COURSE_ACCESS_GRANTED через Kafka.
     *
     * @param user             сущность пользователя, которому предоставлен доступ
     * @param mentor           сущность ментора, предоставившего доступ
     * @param course           сущность курса, к которому предоставлен доступ
     * @param userCourseAccess сущность доступа пользователя к курсу
     */
    public void sendCourseAccessGrantedMessage(
            UserEntity user,
            UserEntity mentor,
            CourseEntity course,
            UserCourseAccessEntity userCourseAccess) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.COURSE_ACCESS_GRANTED,
                baseMapper.mapUserDto(user),
                kafkaMapper.createCourseAccessGrantedNotificationPayload(
                        course.getCourseTitle(),
                        userCourseAccess.getAccessGrantedAt(),
                        baseMapper.mapUserDto(mentor)
                )
        ));
    }

    /**
     * Отправляет уведомление пользователю об отзыве доступа к курсу.
     *
     * @param user             пользователь, у которого отзывается доступ
     * @param mentor           ментор, отозвавший доступ
     * @param course           курс, к которому отозван доступ
     * @param accessRevokedAt  дата и время отзыва доступа
     */
    public void sendCourseAccessRevokedMessage(
            UserEntity user,
            UserEntity mentor,
            CourseEntity course,
            LocalDateTime accessRevokedAt) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.COURSE_ACCESS_REVOKED,
                baseMapper.mapUserDto(user),
                kafkaMapper.createCourseAccessRevokedNotificationPayload(
                        course.getCourseTitle(),
                        accessRevokedAt,
                        baseMapper.mapUserDto(mentor)
                )
        ));
    }

    /**
     * Отправляет сообщение наставнику о создании курса.
     * Создает и отправляет уведомление типа COURSE_CREATED_MENTOR через Kafka.
     * @param course созданный курс (сохраненный в БД с id/title/createdAt)
     * @param recipient получатель сообщения (ментор)
     */
    public void sendCourseCreatedMessage(
            CourseEntity course,
            UserEntity recipient) {
        UserInfoDto recipientInfo = baseMapper.mapUserDto(recipient);
        log.info("Получатель UserInfoDto: {}", recipientInfo);
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.COURSE_CREATED_MENTOR,
                recipientInfo,
                kafkaMapper.courseCreatedMentorNotificationPayload(
                        course.getCourseTitle(),
                        course.getCreatedAt(),
                        recipientInfo)
        ));
    }

    /**
     * Отправляет сообщение наставнику об удалении курса.
     * @param course удаляемый курс
     * @param recipient получатель сообщения (наставник)
     */
    public void sendCourseDeletedMessage(
            CourseEntity course,
            UserEntity recipient) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.COURSE_DELETED,
                baseMapper.mapUserDto(recipient),
                kafkaMapper.courseDeletedMentorNotificationPayload(
                        course.getCourseTitle())
        ));
    }

    /**
     * Отправляет сообщение о предоставлении доступа к модулю.
     * Создает и отправляет уведомление типа MODULE_ACCESS_GRANTED через Kafka.
     *
     * @param user             сущность пользователя, которому предоставлен доступ
     * @param mentor           сущность ментора, предоставившего доступ
     * @param course           сущность курса, к которому относится модуль
     * @param module           сущность модуля, к которому предоставлен доступ
     * @param userModuleAccess сущность доступа пользователя к модулю
     */
    public void sendModuleAccessGrantedMessage(
            UserEntity user,
            UserEntity mentor,
            CourseEntity course,
            ModuleEntity module,
            UserModuleAccessEntity userModuleAccess) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.MODULE_ACCESS_GRANTED,
                baseMapper.mapUserDto(user),
                kafkaMapper.createModuleAccessGrantedNotificationPayload(
                        course.getCourseTitle(),
                        module.getModuleTitle(),
                        userModuleAccess.getAccessGrantedAt(),
                        baseMapper.mapUserDto(mentor)
                )
        ));
    }

    /**
     * Отправляет уведомление пользователю об отзыве доступа к модулю.
     *
     * @param user пользователь, у которого отозвали доступ
     * @param mentor ментор, отозвавший доступ
     * @param course курс, к которому относится модуль
     * @param module модуль, к которому отозван доступ
     * @param accessRevokedAt дата и время отзыва доступа
     */
    public void sendModuleAccessRevokedMessage(
            UserEntity user,
            UserEntity mentor,
            CourseEntity course,
            ModuleEntity module,
            LocalDateTime accessRevokedAt) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.MODULE_ACCESS_REVOKED,
                baseMapper.mapUserDto(user),
                kafkaMapper.createModuleAccessRevokedNotificationPayload(
                        course.getCourseTitle(),
                        module.getModuleTitle(),
                        accessRevokedAt,
                        baseMapper.mapUserDto(mentor)
                )
        ));
    }

    /**
     * Отправляет сообщение наставнику о создании модуля.
     * @param course курс, к которому относится модуль
     * @param module созданный курс (сохраненный в БД с id/title/createdAt)
     * @param recipient получатель сообщения (наставник)
     * @param creator создатель модуля
     */
    public void sendModuleCreatedMessage(
            CourseEntity course,
            ModuleEntity module,
            UserEntity recipient,
            UserEntity creator) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.MODULE_CREATED_MENTOR,
                baseMapper.mapUserDto(recipient),
                kafkaMapper.moduleCreatedMentorNotificationPayload(
                        course.getCourseTitle(),
                        module.getModuleTitle(),
                        module.getCreatedAt(),
                        baseMapper.mapUserDto(creator))
        ));
    }

    /**
     * Отправляет сообщение наставнику об удалении модуля.
     * @param module удаляемый курс
     * @param recipient получатель сообщения (наставник)
     */
    public void sendModuleDeletedMessage(
            CourseEntity course,
            ModuleEntity module,
            UserEntity recipient) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.MODULE_DELETED,
                baseMapper.mapUserDto(recipient),
                kafkaMapper.moduleDeletedMentorNotificationPayload(
                        course.getCourseTitle(),
                        module.getModuleTitle())
        ));
    }

    /**
     * Отправляет сообщение вновь зарегестрированному пользователю.
     * Создает и отправляет уведомление типа USER_REGISTRATION_USER через Kafka.
     */
    public void sendUserRegistrationMessage(UserEntity user) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.USER_REGISTRATION_USER,
                baseMapper.mapUserDto(user),
                kafkaMapper.userRegistrationNotificationPayload(
                        baseMapper.mapUserDto(user)
                )
        ));
    }

    /**
     * Отправляет сообщение наставнику о бронировании временного слота учеником.
     * @param mentor получатель сообщения (наставник)
     * @param mentee ученик, который забронировал встречу
     * @param startAt дата и время начала встречи
     * @param endAt дата и время начала встречи
     */
    public void sendCreateSlotBookedMessage(
            UserInfoDto mentor,
            LocalDateTime startAt,
            LocalDateTime endAt,
            UserInfoDto mentee) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                                          NotificationTypeEnum.SLOT_BOOKED_MENTOR,
                                          mentor,
                                          kafkaMapper.slotBookedNotificationPayload(
                                                  startAt,
                                                  endAt,
                                                  mentee)
                                  )
        );
    }

    /**
     * Отправляет напоминание студенту о предстоящей встрече.
     *
     * @param slot    слот, о котором нужно напомнить
     * @param student студент, которому отправляется напоминание
     */
    public void sendStudentCalendarSlotReminderMessage(
            MentorTimeSlotEntity slot,
            UserEntity student) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER,
                baseMapper.mapUserDto(student),
                kafkaMapper.createStudentReminderNotificationPayload(slot, student)
        ));
    }

    /**
     * Отправляет напоминание ментору о предстоящей встрече.
     *
     * @param slot слот, о котором нужно напомнить
     */
    public void sendMentorCalendarSlotReminderMessage(MentorTimeSlotEntity slot) {
        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
                NotificationTypeEnum.MENTOR_CALENDAR_SLOT_REMINDER,
                baseMapper.mapUserDto(slot.getMentor()),
                kafkaMapper.createMentorReminderNotificationPayload(slot)
        ));
    }

}
