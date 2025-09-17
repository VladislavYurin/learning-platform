package ru.mentor.service.impl;

import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.entity.NotificationEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.KafkaMapper;
import ru.mentor.repository.NotificationRepository;
import ru.mentor.service.EmailSenderService;
import ru.mentor.service.NotificationTemplateService;
import ru.mentor.service.TelegramSenderService;


@ExtendWith(MockitoExtension.class)
class DefaultNotificationServiceImplTest {

    public static final String EMAIL_TEST = "test@test.ru";
    public static final String MESSAGE = "TEXT";
    public static final Long CHAT_ID = 123L;

    private UserEntity userEntity;
    private NotificationEntity notificationEntity;
    private DefaultNotificationServiceImpl service;
    private UserInfoDto userInfoDto;

    @Mock
    EmailSenderService emailSenderService;

    @Mock
    TelegramSenderService telegramSenderService;

    @Mock
    NotificationTemplateService templateService;

    @Mock
    NotificationRepository notificationRepository;

    @Spy
    BaseMapper baseMapper;

    @Spy
    KafkaMapper kafkaMapper;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .username("test")
                .password("psw")
                .role(Role.USER)
                .firstName("Ivan")
                .lastName("Testov")
                .tgNickname("test")
                .tgChatId(CHAT_ID)
                .build();

        notificationEntity = new NotificationEntity();
    }

    /**
     * Проверяет, что уведомление отправляется в Telegram и на Почту
     */
    @Test
    void notifyByTelegramAndEmail() {
        Mockito.when(templateService.generateEmailContent(Mockito.any())).thenReturn(MESSAGE);

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(CHAT_ID);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);
        Mockito.when(templateService.getEmailSubject(Mockito.any())).thenReturn("SUBJ");

        service = new DefaultNotificationServiceImpl(mockProvider(emailSenderService),
                mockProvider(telegramSenderService), templateService, notificationRepository, baseMapper, kafkaMapper);

        service.notifyUser(dto);

        Mockito.verify(telegramSenderService).sendMessage(CHAT_ID, MESSAGE, true);
        Mockito.verify(emailSenderService, Mockito.atLeastOnce())
                .sendEmail(EMAIL_TEST, "SUBJ", MESSAGE);
        Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any());
    }

    /**
     * Проверяет, что уведомление отправляется по email,
     * если ID чата отсутствует.
     */
    @Test
    void notifyByEmail_whenChatIdAbsent() {
        Mockito.when(templateService.generateEmailContent(Mockito.any())).thenReturn(MESSAGE);
        Mockito.when(templateService.getEmailSubject(Mockito.any())).thenReturn("SUBJ");

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(null);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);
        Mockito.when(notificationRepository.save(Mockito.any(NotificationEntity.class))).thenReturn(notificationEntity);

        service = new DefaultNotificationServiceImpl(
                mockProvider(emailSenderService), mockProvider(null), templateService, notificationRepository, baseMapper, kafkaMapper);

        service.notifyUser(dto);

        Mockito.verifyNoInteractions(telegramSenderService);
        Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any());
    }

    /**
     * Проверяет, что уведомление отправляется в Telegram,
     * если EmailSender недоступен.
     */
    @Test
    void notifyByTelegram_whenEmailSenderServiceAbsent() {
        Mockito.when(templateService.generateEmailContent(Mockito.any())).thenReturn(MESSAGE);

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(CHAT_ID);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);

        service = new DefaultNotificationServiceImpl(mockProvider(null),
                mockProvider(telegramSenderService), templateService, notificationRepository, baseMapper, kafkaMapper);

        service.notifyUser(dto);

        Mockito.verify(telegramSenderService).sendMessage(CHAT_ID, MESSAGE, true);
        Mockito.verifyNoInteractions(emailSenderService);
        Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any());
    }

    /**
     * Проверяет сохранение ошибки, если нет ни почты ни Telegram
     */
    @Test
    void notifyUser_whenChatIdAndEmailAbsent_saveNotificationWithErrorMessage(){

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(null);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);

        service = new DefaultNotificationServiceImpl(
                mockProvider(null), mockProvider(null), templateService, notificationRepository, baseMapper, kafkaMapper);

        service.notifyUser(dto);

        Mockito.verifyNoInteractions(telegramSenderService);
        Mockito.verifyNoInteractions(emailSenderService);
        Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any());
    }

    @SuppressWarnings("unchecked")
    private <T> ObjectProvider<T> mockProvider(T bean) {
        ObjectProvider<T> provider = Mockito.mock(ObjectProvider.class);
        Mockito.when(provider.getIfAvailable()).thenReturn(bean);
        return provider;
    }
}
