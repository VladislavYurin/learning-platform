package ru.mentor.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentor.constant.NotificationDestination;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.constant.Role;
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
import ru.mentor.testUtil.TestDataGenerator;

class KafkaMapperTest {

    private static final LocalDateTime FIXED = LocalDateTime.of(2026, 4, 2, 18, 0);

    private KafkaMapper kafkaMapper;

    @BeforeEach
    void setUp() {
        kafkaMapper = new KafkaMapperImpl();
    }

    private static UserInfoDto sampleUser() {
        return UserInfoDto.builder()
                .id(1L)
                .username("mentor")
                .role(Role.MENTOR)
                .firstName("F")
                .lastName("L")
                .tgNickname("@m")
                .build();
    }

    @Test
    void createKafkaNotificationDto_validData_success() {
        NotificationTypeEnum notificationType = NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER;
        UserInfoDto userInfoDto = sampleUser();
        NotificationPayload payload = new NotificationPayload() {};

        KafkaNotificationDto result = kafkaMapper.createKafkaNotificationDto(notificationType,
                userInfoDto, payload);

        Assertions.assertEquals(notificationType, result.getNotificationType());
        Assertions.assertEquals(userInfoDto, result.getUserInfo());
        Assertions.assertEquals(payload, result.getPayload());
    }

    @Test
    void createCourseAccessGrantedNotificationPayload_mapsFields() {
        UserInfoDto by = sampleUser();

        CourseAccessGrantedNotificationPayload p =
                kafkaMapper.createCourseAccessGrantedNotificationPayload("Course A", FIXED, by);

        Assertions.assertEquals("Course A", p.getCourseTitle());
        Assertions.assertEquals(FIXED, p.getAccessGrantedAt());
        Assertions.assertEquals(by, p.getAccessGrantedBy());
    }

    @Test
    void createCourseAccessRevokedNotificationPayload_mapsFields() {
        UserInfoDto by = sampleUser();

        CourseAccessRevokedNotificationPayload p =
                kafkaMapper.createCourseAccessRevokedNotificationPayload("Course B", FIXED, by);

        Assertions.assertEquals("Course B", p.getCourseTitle());
        Assertions.assertEquals(FIXED, p.getAccessRevokedAt());
        Assertions.assertEquals(by, p.getAccessRevokedBy());
    }

    @Test
    void courseCreatedMentorNotificationPayload_mapsFields() {
        UserInfoDto by = sampleUser();

        CourseCreatedMentorNotificationPayload p =
                kafkaMapper.courseCreatedMentorNotificationPayload("New", FIXED, by);

        Assertions.assertEquals("New", p.getCourseTitle());
        Assertions.assertEquals(FIXED, p.getCreatedAt());
        Assertions.assertEquals(by, p.getCourseCreatedBy());
    }

    @Test
    void courseDeletedMentorNotificationPayload_mapsTitle() {
        CourseDeletedMentorNotificationPayload p =
                kafkaMapper.courseDeletedMentorNotificationPayload("Old");

        Assertions.assertEquals("Old", p.getCourseTitle());
    }

    @Test
    void createModuleAccessGrantedNotificationPayload_mapsFields() {
        UserInfoDto by = sampleUser();

        ModuleAccessGrantedNotificationPayload p =
                kafkaMapper.createModuleAccessGrantedNotificationPayload("C", "M", FIXED, by);

        Assertions.assertEquals("C", p.getCourseTitle());
        Assertions.assertEquals("M", p.getModuleTitle());
        Assertions.assertEquals(FIXED, p.getAccessGrantedAt());
        Assertions.assertEquals(by, p.getAccessGrantedBy());
    }

    @Test
    void createModuleAccessRevokedNotificationPayload_mapsFields() {
        UserInfoDto by = sampleUser();

        ModuleAccessRevokedNotificationPayload p =
                kafkaMapper.createModuleAccessRevokedNotificationPayload("C", "M", FIXED, by);

        Assertions.assertEquals("C", p.getCourseTitle());
        Assertions.assertEquals("M", p.getModuleTitle());
        Assertions.assertEquals(FIXED, p.getAccessRevokedAt());
        Assertions.assertEquals(by, p.getAccessRevokedBy());
    }

    @Test
    void moduleCreatedMentorNotificationPayload_mapsFields() {
        UserInfoDto by = sampleUser();

        ModuleCreatedMentorNotificationPayload p =
                kafkaMapper.moduleCreatedMentorNotificationPayload("C", "M", FIXED, by);

        Assertions.assertEquals("C", p.getCourseTitle());
        Assertions.assertEquals("M", p.getModuleTitle());
        Assertions.assertEquals(FIXED, p.getCreatedAt());
        Assertions.assertEquals(by, p.getModuleCreatedBy());
    }

    @Test
    void moduleDeletedMentorNotificationPayload_mapsTitles() {
        ModuleDeletedMentorNotificationPayload p =
                kafkaMapper.moduleDeletedMentorNotificationPayload("C", "M");

        Assertions.assertEquals("C", p.getCourseTitle());
        Assertions.assertEquals("M", p.getModuleTitle());
    }

    @Test
    void userRegistrationNotificationPayload_setsUserInfoAndCreatedAt() {
        UserInfoDto user = sampleUser();

        UserRegistrationNotificationPayload p = kafkaMapper.userRegistrationNotificationPayload(user);

        Assertions.assertEquals(user, p.getUserInfo());
        Assertions.assertNotNull(p.getCreatedAt());
    }

    @Test
    void mapNotificationEntity_buildsEntityFromDto() {
        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.COURSE_ACCESS_GRANTED)
                .build();
        UserEntity recipient = TestDataGenerator.getTestParticipantUser();

        NotificationEntity entity = kafkaMapper.mapNotificationEntity(
                dto,
                NotificationDestination.MAIL,
                "err",
                NotificationStatus.OK,
                recipient);

        Assertions.assertEquals(NotificationTypeEnum.COURSE_ACCESS_GRANTED, entity.getNotificationType());
        Assertions.assertEquals(recipient, entity.getRecipient());
        Assertions.assertEquals(NotificationDestination.MAIL, entity.getNotificationDestination());
        Assertions.assertEquals(NotificationStatus.OK, entity.getNotificationStatus());
    }

    @Test
    void slotBookedNotificationPayload_mapsTimesAndMentee() {
        UserInfoDto mentee = sampleUser();
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 1, 11, 0);

        SlotBookedNotificationPayload p =
                kafkaMapper.slotBookedNotificationPayload(start, end, mentee);

        Assertions.assertEquals(start, p.getStartAt());
        Assertions.assertEquals(end, p.getEndAt());
        Assertions.assertEquals(mentee, p.getMentee());
    }

    @Test
    void createStudentReminderNotificationPayload_mapsFromSlot() {
        UserEntity student = TestDataGenerator.getTestParticipantUser();
        MentorTimeSlotEntity slot = TestDataGenerator.createTestSlot(1L, Set.of(student), true);

        StudentReminderNotificationPayload result =
                kafkaMapper.createStudentReminderNotificationPayload(slot, student);

        Assertions.assertEquals(TestDataGenerator.TEST_USER_FIRST_NAME, result.getStudentName());
        Assertions.assertEquals(TestDataGenerator.DEFAULT_START_TIME, result.getCalendarSlotTime());
        Assertions.assertEquals(TestDataGenerator.TEST_MENTOR_FIRST_NAME, result.getMentorName());
    }

    @Test
    void createMentorReminderNotificationPayload_collectsParticipantFirstNames() {
        UserEntity u1 = TestDataGenerator.getTestParticipantUser();
        UserEntity u2 = TestDataGenerator.getAnotherTestParticipantUser();
        MentorTimeSlotEntity slot = TestDataGenerator.createTestSlot(2L, Set.of(u1, u2), true);

        MentorReminderNotificationPayload p = kafkaMapper.createMentorReminderNotificationPayload(slot);

        Assertions.assertEquals(TestDataGenerator.TEST_MENTOR_FIRST_NAME, p.getMentorName());
        List<String> names = p.getStudentNames();
        Assertions.assertEquals(2, names.size());
        Assertions.assertTrue(names.contains(TestDataGenerator.TEST_USER_FIRST_NAME));
        Assertions.assertTrue(names.contains("Ivan"));
    }
}
