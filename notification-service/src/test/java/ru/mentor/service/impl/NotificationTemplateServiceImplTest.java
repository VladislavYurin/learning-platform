package ru.mentor.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.mentor.cache.NotificationCacheProcessor;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import ru.mentor.dto.kafka.*;
import ru.mentor.exception.EntityNotFoundException;


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
    void generateEmailContent_courseCreated_success(){
        String template = "Уважаемый, %s! Создан новый курс \"%s\". Автор курса: %s %s. Получатель: %s %s. Дата создания: %s.";
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.COURSE_CREATED_MENTOR))
                .thenReturn(template);
        UserInfoDto mentor = UserInfoDto.builder().id(1L).firstName("Макс").lastName("Админов").build();

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.COURSE_CREATED_MENTOR)
                .userInfo(mentor)
                .payload(CourseCreatedMentorNotificationPayload.builder()
                        .courseTitle("Java Basics")
                        .courseCreatedBy(mentor)
                        .createdAt(LocalDateTime.of(2025, 9, 4, 15, 0))
                        .recipientUser(mentor)
                        .build())

                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Уважаемый, Макс! Создан новый курс \"Java Basics\". Автор курса: Макс Админов. " +
                          "Получатель: Макс Админов. Дата создания: 04.09.2025 15:00.";

        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_moduleCreated_success() {
    String template = "Уважаемый, %s! Создан новый модуль \"%s\" в курсе \"%s\". Автор модуля: %s %s." +
                      " Получатель: %s %s. Дата создания: %s.";

    Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.MODULE_CREATED_MENTOR))
            .thenReturn(template);

    UserInfoDto mentor = UserInfoDto.builder().id(1L).firstName("Макс").lastName("Админов").build();

    KafkaNotificationDto dto = KafkaNotificationDto.builder()
            .notificationType(NotificationTypeEnum.MODULE_CREATED_MENTOR)
            .userInfo(mentor)
            .payload(ModuleCreatedMentorNotificationPayload.builder()
                    .moduleTitle("Generics")
                    .courseTitle("Java Basic")
                    .moduleCreatedBy(mentor)
                    .createdAt(LocalDateTime.of(2025, 9, 4, 15, 0))
                    .recipientUser(mentor)
                    .build())
            .build();
    String content = service.generateEmailContent(dto);

    String expected = "Уважаемый, Макс! Создан новый модуль \"Generics\" в курсе \"Java Basic\". " +
                      "Автор модуля: Макс Админов. Получатель: Макс Админов. Дата создания: 04.09.2025 15:00.";

    Assertions.assertEquals(expected, content);

    }

    @Test
    void generateEmailContent_courseDeleted_success() {
        String template = "Уважаемый, %s! Курс \"%s\" удален.";

        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.COURSE_DELETED))
                .thenReturn(template);

        UserInfoDto mentor = UserInfoDto.builder().id(1L).firstName("Макс").lastName("Админов").build();

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.COURSE_DELETED)
                .userInfo(mentor)
                .payload(CourseDeletedMentorNotificationPayload.builder()
                        .courseTitle("Java Basic")
                        .build()
                )
        .build();

        String content = service.generateEmailContent(dto);

        String expected = "Уважаемый, Макс! Курс \"Java Basic\" удален.";

        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_moduleDeleted_success() {
        String template = "Уважаемый, %s! Модуль \"%s\" удален.";

        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.MODULE_DELETED))
                .thenReturn(template);

        UserInfoDto mentor = UserInfoDto.builder().id(1L).firstName("Макс").lastName("Админов").build();

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.MODULE_DELETED)
                .userInfo(mentor)
                .payload(ModuleDeletedMentorNotificationPayload.builder()
                        .moduleTitle("Collections")
                        .build()
                )
                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Уважаемый, Макс! Модуль \"Collections\" удален.";

        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_userRegistration_success() {
        // Arrange
        String template = "Уважаемый, %s!\nВы успешно зарегистрированы!\nДата создания: %s.\n";
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.USER_REGISTRATION_USER))
                .thenReturn(template);

        LocalDateTime createdAt = LocalDateTime.of(2025, 9, 9, 12, 0);
        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.USER_REGISTRATION_USER)
                .userInfo(UserInfoDto.builder()
                        .id(5L)
                        .username("new_user")
                        .firstName("Русификат")
                        .lastName("Православов")
                        .build())
                .payload(UserRegistrationNotificationPayload.builder()
                        .userName("Русификат Православов")
                        .createdAt(createdAt)
                        .build())
                .build();

        // Act
        String content = service.generateEmailContent(dto);

        // Assert
        String expected = "Уважаемый, Русификат!\nВы успешно зарегистрированы!\nДата создания: 09.09.2025 12:00.\n";
        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_courseAccessRevoked_success() {
        String template = "Здравствуйте, %s! У вас отозван доступ к курсу %s пользователем %s %s %s.";
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.COURSE_ACCESS_REVOKED))
                .thenReturn(template);

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.COURSE_ACCESS_REVOKED)
                .userInfo(UserInfoDto.builder()
                        .id(1L)
                        .username("ivan123")
                        .firstName("Иван")
                        .lastName("Иванов")
                        .build())
                .payload(CourseAccessRevokedNotificationPayload.builder()
                        .courseTitle("Java Basics")
                        .accessRevokedBy(UserInfoDto.builder()
                                .id(2L)
                                .username("admin")
                                .firstName("Админ")
                                .lastName("Системов")
                                .build())
                        .accessRevokedAt(LocalDateTime.of(2025, 9, 10, 14, 30))
                        .build())
                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Здравствуйте, Иван! У вас отозван доступ к курсу Java Basics пользователем Админ Системов 10.09.2025 14:30.";
        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_moduleAccessRevoked_success() {
        String template = "Здравствуйте, %s! У вас отозван доступ к модулю %s курса %s пользователем %s %s %s.";
        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.MODULE_ACCESS_REVOKED))
                .thenReturn(template);

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.MODULE_ACCESS_REVOKED)
                .userInfo(UserInfoDto.builder()
                        .id(3L)
                        .username("maria123")
                        .firstName("Мария")
                        .lastName("Петрова")
                        .build())
                .payload(ModuleAccessRevokedNotificationPayload.builder()
                        .moduleTitle("Collections")
                        .courseTitle("Java Advanced")
                        .accessRevokedBy(UserInfoDto.builder()
                                .id(4L)
                                .username("igor_dev")
                                .firstName("Игорь")
                                .lastName("Разраб")
                                .build())
                        .accessRevokedAt(LocalDateTime.of(2025, 9, 10, 16, 45))
                        .build())
                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Здравствуйте, Мария! У вас отозван доступ к модулю Collections курса Java Advanced пользователем Игорь Разраб 10.09.2025 16:45.";
        Assertions.assertEquals(expected, content);
    }

    @Test
    void generateEmailContent_slotBooked_success(){
        String template = "Уважаемый, %s! Слот на дату: %s - %s забронирован пользователем: %s %s.";

        Mockito.when(cacheProcessor.getTemplateCache(NotificationTypeEnum.SLOT_BOOKED_MENTOR))
                .thenReturn(template);

        UserInfoDto mentor = UserInfoDto.builder().id(1L).firstName("Макс").lastName("Админов").build();
        UserInfoDto mentee = UserInfoDto.builder().id(100L).firstName("Любовь").lastName("Александрова").build();

        LocalDateTime startAt = LocalDateTime.of(2025, 9, 11, 15, 5);
        LocalDateTime endAt = LocalDateTime.of(2025, 9, 11, 15, 30);

        KafkaNotificationDto dto = KafkaNotificationDto.builder()
                .notificationType(NotificationTypeEnum.SLOT_BOOKED_MENTOR)
                .userInfo(mentor)
                .payload(SlotBookedNotificationPayload.builder()
                        .startAt(startAt)
                        .endAt(endAt)
                        .mentor(mentor)
                        .mentee(mentee)
                        .build())
                .build();

        String content = service.generateEmailContent(dto);

        String expected = "Уважаемый, Макс! Слот на дату: 11.09.2025 15:05 - 11.09.2025 15:30 " +
                          "забронирован пользователем: Любовь Александрова.";
        Assertions.assertEquals(expected, content);
    }

    @Test
    void getEmailSubject_returnsCorrectSubject() {
        Assertions.assertEquals("Доступ к курсу",
                service.getEmailSubject(NotificationTypeEnum.COURSE_ACCESS_GRANTED));
        Assertions.assertEquals("Новый модуль доступен",
                service.getEmailSubject(NotificationTypeEnum.MODULE_ACCESS_GRANTED));
        Assertions.assertEquals("Создан новый курс",
                service.getEmailSubject(NotificationTypeEnum.COURSE_CREATED_MENTOR));
        Assertions.assertEquals("Создан новый модуль",
                service.getEmailSubject(NotificationTypeEnum.MODULE_CREATED_MENTOR));
        Assertions.assertEquals("Удален курс",
                service.getEmailSubject(NotificationTypeEnum.COURSE_DELETED));
        Assertions.assertEquals("Новый пользователь успешно зарегистрирован",
                service.getEmailSubject(NotificationTypeEnum.USER_REGISTRATION_USER));
        Assertions.assertEquals("Доступ к курсу отозван",
                service.getEmailSubject(NotificationTypeEnum.COURSE_ACCESS_REVOKED));
        Assertions.assertEquals("Доступ к модулю отозван",
                service.getEmailSubject(NotificationTypeEnum.MODULE_ACCESS_REVOKED));
        Assertions.assertEquals("Модуль удален",
                service.getEmailSubject(NotificationTypeEnum.MODULE_DELETED));
        Assertions.assertEquals("Забронирован слот",
                service.getEmailSubject(NotificationTypeEnum.SLOT_BOOKED_MENTOR));
    }
}
