package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.NotificationPayload;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.grpc.HeaderFactory;
import ru.mentor.testUtil.TestDataGenerator;
import java.util.Set;

class KafkaMapperTest {
    @Mock
    private HeaderFactory headerFactory;
    private KafkaMapper kafkaMapper;
    private BaseMapper baseMapper;

    @BeforeEach
    void setUp() {
        kafkaMapper = new KafkaMapper();
        baseMapper = new BaseMapper(headerFactory);
    }

    @Test
    void createKafkaNotificationDto_validData_success() {
        NotificationTypeEnum notificationType = NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER;
        UserInfoDto userInfoDto = baseMapper.mapUserDto(TestDataGenerator.getTestParticipantUser());
        NotificationPayload payload = new NotificationPayload() {};

        KafkaNotificationDto result = kafkaMapper.createKafkaNotificationDto(notificationType,
                userInfoDto, payload);

        Assertions.assertEquals(notificationType, result.getNotificationType());
        Assertions.assertEquals(userInfoDto, result.getUserInfo());
        Assertions.assertEquals(payload, result.getPayload());
    }

    @Test
    void createStudentReminderNotificationPayload() {
        UserEntity student = TestDataGenerator.getTestParticipantUser();
        MentorTimeSlotEntity mentorTimeSlotEntity = TestDataGenerator.createTestSlot(1L, Set.of(student), true);

        StudentReminderNotificationPayload result =
                kafkaMapper.createStudentReminderNotificationPayload(mentorTimeSlotEntity, student);

        Assertions.assertEquals(TestDataGenerator.TEST_USER_FIRST_NAME, result.getStudentName());
        Assertions.assertEquals(TestDataGenerator.DEFAULT_START_TIME, result.getCalendarSlotTime());
        Assertions.assertEquals(TestDataGenerator.TEST_MENTOR_FIRST_NAME, result.getMentorName());
        Assertions.assertEquals(CalendarSlotMeetingType.COMMUNICATION.toString(), result.getSlotMeetingType());
        Assertions.assertEquals(CalendarSlotType.INDIVIDUAL.toString(), result.getSlotType());
        Assertions.assertEquals(TestDataGenerator.TEST_DESCRIPTION, result.getDescription());
        Assertions.assertEquals(TestDataGenerator.TEST_LINK, result.getMeetingLink());
    }
}