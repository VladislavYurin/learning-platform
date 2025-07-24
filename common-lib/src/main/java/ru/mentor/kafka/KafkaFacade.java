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

@Service
@RequiredArgsConstructor
public class KafkaFacade {

    private final KafkaProducerService kafkaProducerService;

    private final KafkaMapper kafkaMapper;

    private final BaseMapper baseMapper;

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
