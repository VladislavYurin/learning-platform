package ru.mentor.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.entity.NotificationEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.KafkaMapper;
import ru.mentor.mapper.UtilMapper;
import ru.mentor.repository.NotificationRepository;
import ru.mentor.service.EmailSenderService;
import ru.mentor.service.NotificationTemplateService;
import ru.mentor.service.TelegramSenderService;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;

@ExtendWith(MockitoExtension.class)
class DefaultNotificationServiceImplTest {

    private String EMAIL_TEST = "test@test.ru";
    private String MESSAGE = "TEXT";
    private String SUBJ = "SUBJ";
    private Long CHAT_ID = TestConstantHolder.tgChatId;
    private NotificationEntity notificationEntity;
    private DefaultNotificationServiceImpl service;

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

    @Mock
    private UtilMapper utilMapper;

    @BeforeEach
    void setUp() {
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
        Mockito.when(templateService.getEmailSubject(Mockito.any())).thenReturn(SUBJ);

        service = new DefaultNotificationServiceImpl(
                mockProvider(emailSenderService),
                mockProvider(telegramSenderService),
                templateService,
                notificationRepository,
                baseMapper,
                utilMapper,
                kafkaMapper);

        service.notifyUser(dto);

        Mockito.verify(telegramSenderService).sendMessage(CHAT_ID, MESSAGE, true);
        Mockito.verify(emailSenderService, Mockito.atLeastOnce())
                .sendEmail(EMAIL_TEST, SUBJ, MESSAGE);
        Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any());
    }

    /**
     * Проверяет, что уведомление отправляется по email,
     * если ID чата отсутствует.
     */
    @Test
    void notifyByEmail_whenChatIdAbsent() {
        Mockito.when(templateService.generateEmailContent(Mockito.any())).thenReturn(MESSAGE);
        Mockito.when(templateService.getEmailSubject(Mockito.any())).thenReturn(SUBJ);

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(null);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);
        Mockito.when(notificationRepository.save(Mockito.any(NotificationEntity.class)))
                .thenReturn(notificationEntity);

        service = new DefaultNotificationServiceImpl(
                mockProvider(emailSenderService),
                mockProvider(null),
                templateService,
                notificationRepository,
                baseMapper,
                utilMapper,
                kafkaMapper);

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

        service = new DefaultNotificationServiceImpl(
                mockProvider(null),
                mockProvider(telegramSenderService),
                templateService,
                notificationRepository,
                baseMapper,
                utilMapper,
                kafkaMapper);

        service.notifyUser(dto);

        Mockito.verify(telegramSenderService).sendMessage(CHAT_ID, MESSAGE, true);
        Mockito.verifyNoInteractions(emailSenderService);
        Mockito.verify(notificationRepository, Mockito.atLeastOnce()).save(Mockito.any());
    }

    /**
     * Проверяет сохранение ошибки, если нет ни почты ни Telegram
     */
    @Test
    void notifyUser_whenChatIdAndEmailAbsent_saveNotificationWithErrorMessage() {

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(null);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);

        service = new DefaultNotificationServiceImpl(
                mockProvider(null),
                mockProvider(null),
                templateService,
                notificationRepository,
                baseMapper,
                utilMapper,
                kafkaMapper);

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
