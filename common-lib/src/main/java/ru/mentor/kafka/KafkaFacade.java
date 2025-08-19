package ru.mentor.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserCourseAccessEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.entity.UserModuleAccessEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.KafkaMapper;

/**
 * Фасадный сервис для отправки сообщений в Kafka.
 * Предоставляет удобные методы для отправки различных типов уведомлений,
 * связанных с доступом пользователей к курсам и модулям.
 */
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
     * Отправляет сообщение о предоставлении доступа к модулю.
     * Создает и отправляет уведомление типа MODULE_ACCESS_GRANTED через Kafka.
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

}
