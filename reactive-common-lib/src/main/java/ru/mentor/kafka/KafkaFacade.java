//package ru.mentor.kafka;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import ru.mentor.constant.NotificationTypeEnum;
//import ru.mentor.entity.CourseEntity;
//import ru.mentor.entity.MentorTimeSlotEntity;
//import ru.mentor.entity.ModuleEntity;
//import ru.mentor.entity.UserCourseAccessEntity;
//import ru.mentor.entity.UserEntity;
//import ru.mentor.entity.UserModuleAccessEntity;
//import ru.mentor.mapper.BaseMapper;
//import ru.mentor.mapper.KafkaMapper;
//
///**
// * Фасадный сервис для отправки сообщений в Kafka.
// * Предоставляет удобные методы для отправки различных типов уведомлений,
// * связанных с доступом пользователей к курсам и модулям.
// */
//@Service
//@RequiredArgsConstructor
//public class KafkaFacade {
//
//    /**
//     * Сервис для отправки сообщений в Kafka.
//     */
//    private final KafkaProducerService kafkaProducerService;
//
//    /**
//     * Маппер для преобразования сущностей в DTO для Kafka сообщений.
//     */
//    private final KafkaMapper kafkaMapper;
//
//    /**
//     * Базовый маппер для преобразования сущностей в DTO.
//     */
//    private final BaseMapper baseMapper;
//
//    /**
//     * Отправляет сообщение о предоставлении доступа к курсу.
//     * Создает и отправляет уведомление типа COURSE_ACCESS_GRANTED через Kafka.
//     *
//     * @param user             сущность пользователя, которому предоставлен доступ
//     * @param mentor           сущность ментора, предоставившего доступ
//     * @param course           сущность курса, к которому предоставлен доступ
//     * @param userCourseAccess сущность доступа пользователя к курсу
//     */
//    public void sendCourseAccessGrantedMessage(
//            UserEntity user,
//            UserEntity mentor,
//            CourseEntity course,
//            UserCourseAccessEntity userCourseAccess) {
//        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
//                NotificationTypeEnum.COURSE_ACCESS_GRANTED,
//                baseMapper.mapUserDto(user),
//                kafkaMapper.createCourseAccessGrantedNotificationPayload(
//                        course.getCourseTitle(),
//                        userCourseAccess.getAccessGrantedAt(),
//                        baseMapper.mapUserDto(mentor)
//                )
//        ));
//    }
//
//    /**
//     * Отправляет сообщение о предоставлении доступа к модулю.
//     * Создает и отправляет уведомление типа MODULE_ACCESS_GRANTED через Kafka.
//     *
//     * @param user             сущность пользователя, которому предоставлен доступ
//     * @param mentor           сущность ментора, предоставившего доступ
//     * @param course           сущность курса, к которому относится модуль
//     * @param module           сущность модуля, к которому предоставлен доступ
//     * @param userModuleAccess сущность доступа пользователя к модулю
//     */
//    public void sendModuleAccessGrantedMessage(
//            UserEntity user,
//            UserEntity mentor,
//            CourseEntity course,
//            ModuleEntity module,
//            UserModuleAccessEntity userModuleAccess) {
//        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
//                NotificationTypeEnum.MODULE_ACCESS_GRANTED,
//                baseMapper.mapUserDto(user),
//                kafkaMapper.createModuleAccessGrantedNotificationPayload(
//                        course.getCourseTitle(),
//                        module.getModuleTitle(),
//                        userModuleAccess.getAccessGrantedAt(),
//                        baseMapper.mapUserDto(mentor)
//                )
//        ));
//    }
//
//    /**
//     * Отправляет напоминание студенту о предстоящей встрече.
//     *
//     * @param slot    слот, о котором нужно напомнить
//     * @param student студент, которому отправляется напоминание
//     */
//    public void sendStudentCalendarSlotReminderMessage(
//            MentorTimeSlotEntity slot,
//            UserEntity student) {
//        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
//                NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER,
//                baseMapper.mapUserDto(student),
//                kafkaMapper.createStudentReminderNotificationPayload(slot, student)
//        ));
//    }
//
//    /**
//     * Отправляет напоминание ментору о предстоящей встрече.
//     *
//     * @param slot слот, о котором нужно напомнить
//     */
//    public void sendMentorCalendarSlotReminderMessage(MentorTimeSlotEntity slot) {
//        kafkaProducerService.send(kafkaMapper.createKafkaNotificationDto(
//                NotificationTypeEnum.MENTOR_CALENDAR_SLOT_REMINDER,
//                baseMapper.mapUserDto(slot.getMentor()),
//                kafkaMapper.createMentorReminderNotificationPayload(slot)
//        ));
//    }
//
//}
