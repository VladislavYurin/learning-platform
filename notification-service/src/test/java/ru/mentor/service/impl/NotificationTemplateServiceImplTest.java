package ru.mentor.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.mentor.cache.NotificationCacheProcessor;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.ModuleAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.exception.EntityNotFoundException;

import java.time.LocalDateTime;


class NotificationTemplateServiceImplTest {

    private NotificationCacheProcessor cacheProcessor;
    private NotificationTemplateServiceImpl service;

    @BeforeEach
    void setUp() {
        cacheProcessor = Mockito.mock(NotificationCacheProcessor.class);
        service = new NotificationTemplateServiceImpl(cacheProcessor);
    }

    @Test
    void generateEmailContent_courseAccessGranted_success() {
        String template = "Здравствуйте, %s! Вам открыт доступ к курсу %s пользователем %s %s %s.";
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.COURSE_ACCESS_GRANTED))
                .thenReturn(template);

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.COURSE_ACCESS_GRANTED)
                .userInfo(UserInfoDto.builder()
                        .id(1L)
                        .username("ivan123")
                        .firstName("Иван")
                        .lastName("Иванов")
                        .build())
                .payload(CourseAccessGrantedNotificationPayload.builder()
                        .courseTitle("Java Basics")
                        .accessGrantedBy(UserInfoDto.builder()
                                .id(2L)
                                .username("admin")
                                .firstName("Админ")
                                .lastName("Системов")
                                .build())
                        .accessGrantedAt(LocalDateTime.of(2025, 8, 25, 10, 30))
                        .build())
                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Здравствуйте, Иван! Вам открыт доступ к курсу Java Basics пользователем Админ Системов 25.08.2025 10:30.";
        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_moduleAccessGranted_success() {
        String template = "Здравствуйте, %s! Вам доступен модуль %s курса %s от %s %s %s.";
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.MODULE_ACCESS_GRANTED))
                .thenReturn(template);

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.MODULE_ACCESS_GRANTED)
                .userInfo(UserInfoDto.builder()
                        .id(3L)
                        .username("maria123")
                        .firstName("Мария")
                        .lastName("Петрова")
                        .build())
                .payload(ModuleAccessGrantedNotificationPayload.builder()
                        .moduleTitle("Collections")
                        .courseTitle("Java Advanced")
                        .accessGrantedBy(UserInfoDto.builder()
                                .id(4L)
                                .username("igor_dev")
                                .firstName("Игорь")
                                .lastName("Разраб")
                                .build())
                        .accessGrantedAt(LocalDateTime.of(2025, 8, 25, 15, 45))
                        .build())
                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Здравствуйте, Мария! Вам доступен модуль Collections курса Java Advanced от Игорь Разраб 25.08.2025 15:45.";
        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_generatedStudentReminderText_success() {
        String template = """
            Уважаемый %s!
            Напоминаем о запланированной на %s
            встрече с ментором %s
            Тип встречи: %s
            Формат встречи: %s
            Описание встречи: %s
            Ссылка на встречу: %s
        """;
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER))
                .thenReturn(template);

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER)
                .userInfo(UserInfoDto.builder()
                        .firstName("Maksim")
                        .build())
                .payload(StudentReminderNotificationPayload.builder()
                        .calendarSlotTime(LocalDateTime.of(2025, 11, 25, 15, 45))
                        .mentorName("MentorName")
                        .slotMeetingType("Знакомство")
                        .slotType("Индивидуальный")
                        .description("Первая встреча")
                        .meetingLink("https://www.google.com")
                        .build())
                .build();

        String content = service.generateEmailContent(dto);
        String expected = """
            Уважаемый Maksim!
            Напоминаем о запланированной на 25.11.2025 15:45
            встрече с ментором MentorName
            Тип встречи: Знакомство
            Формат встречи: Индивидуальный
            Описание встречи: Первая встреча
            Ссылка на встречу: https://www.google.com
        """;

        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_templateNotFound_throwsException() {
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.COURSE_ACCESS_GRANTED))
                .thenThrow(new EntityNotFoundException("Template with type COURSE_ACCESS_GRANTED not found"));

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.COURSE_ACCESS_GRANTED)
                .userInfo(UserInfoDto.builder()
                        .id(99L)
                        .username("test_user")
                        .firstName("Тест")
                        .lastName("Юзер")
                        .build())
                .payload(CourseAccessGrantedNotificationPayload.builder()
                        .courseTitle("Dummy")
                        .accessGrantedBy(UserInfoDto.builder()
                                .id(100L)
                                .username("admin")
                                .firstName("Админ")
                                .lastName("Тестов")
                                .build())
                        .accessGrantedAt(LocalDateTime.now())
                        .build())
                .build();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.generateEmailContent(dto));
    }

    @Test
    void getEmailSubject_returnsCorrectSubject() {
        Assertions.assertAll(
                () -> Assertions.assertEquals("Доступ к курсу", service.getEmailSubject(NotificationTypeEnum.COURSE_ACCESS_GRANTED)),
                () -> Assertions.assertEquals("Новый модуль доступен", service.getEmailSubject(NotificationTypeEnum.MODULE_ACCESS_GRANTED)),
                () -> Assertions.assertEquals("Напоминание о встрече", service.getEmailSubject(NotificationTypeEnum.MENTOR_CALENDAR_SLOT_REMINDER)),
                () -> Assertions.assertEquals("Напоминание о встрече", service.getEmailSubject(NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER))
        );
    }
}
