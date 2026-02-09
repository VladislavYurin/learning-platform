package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.NotificationPayload;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;

import java.util.Set;

class KafkaMapperTest {

    @Mock
    private UtilMapper utilMapper;
    private KafkaMapper kafkaMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaMapper = new KafkaMapper();
    }

    @Test
    void createKafkaNotificationDto_validData_success() {
        NotificationTypeEnum notificationType = NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER;
        UserInfoDto userInfoDto = utilMapper.userEntityToUserInfoDto(TestDataGenerator.getUserEntity());
        NotificationPayload payload = new NotificationPayload() {
        };

        KafkaNotificationDto result = kafkaMapper.createKafkaNotificationDto(notificationType,
                userInfoDto, payload);

        Assertions.assertEquals(notificationType, result.getNotificationType());
        Assertions.assertEquals(userInfoDto, result.getUserInfo());
        Assertions.assertEquals(payload, result.getPayload());
    }

    @Test
    void createStudentReminderNotificationPayload() {
        UserEntity student = TestDataGenerator.getUserEntity();
        MentorTimeSlotEntity mentorTimeSlotEntity = TestDataGenerator.createTestSlot(
                TestConstantHolder.mentorId,
                TestConstantHolder.isActiveTrue,
                Set.of(student));

        StudentReminderNotificationPayload result =
                kafkaMapper.createStudentReminderNotificationPayload(mentorTimeSlotEntity, student);

        Assertions.assertEquals(TestConstantHolder.userFirstName, result.getStudentName());
        Assertions.assertEquals(TestConstantHolder.startTime, result.getCalendarSlotTime());
        Assertions.assertEquals(TestConstantHolder.mentorFirstName, result.getMentorName());
        Assertions.assertEquals(CalendarSlotMeetingType.COMMUNICATION.toString(), result.getSlotMeetingType());
        Assertions.assertEquals(CalendarSlotType.INDIVIDUAL.toString(), result.getSlotType());
        Assertions.assertEquals(TestConstantHolder.slotDescription, result.getDescription());
        Assertions.assertEquals(TestConstantHolder.meetingLink, result.getMeetingLink());
    }
}