package ru.mentor.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.service.EmailSenderService;
import ru.mentor.service.NotificationTemplateService;
import ru.mentor.service.TelegramSenderService;

@ExtendWith(MockitoExtension.class)
class DefaultNotificationServiceImplTest {

    public static final String EMAIL_TEST = "test@test.ru";
    public static final String MESSAGE = "TEXT";
    public static final Long CHAT_ID = 123L;

    @Mock
    EmailSenderService emailSenderService;

    @Mock
    TelegramSenderService telegramSenderService;

    @Mock
    NotificationTemplateService templateService;

    /**
     * Проверяет, что уведомление отправляется в Telegram,
     * если ID чата присутствует.
     */
    @Test
    void notifyByTelegram_whenChatIdPresent_sendsToTelegram() {
        Mockito.when(templateService.generateEmailContent(Mockito.any())).thenReturn(MESSAGE);

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(CHAT_ID);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);
        Mockito.when(templateService.getEmailSubject(Mockito.any())).thenReturn("SUBJ");

        var service = new DefaultNotificationServiceImpl(emailSenderService, telegramSenderService, templateService);

        service.notifyUser(dto);

        Mockito.verify(telegramSenderService).sendMessage(CHAT_ID, MESSAGE, true);
        Mockito.verify(emailSenderService, Mockito.atLeastOnce())
                .sendEmail(EMAIL_TEST, "SUBJ", MESSAGE);
    }

    /**
     * Проверяет, что уведомление отправляется по email,
     * если ID чата отсутствует.
     */
    @Test
    void notifyByTelegram_whenChatIdAbsent_fallbacksToEmail() {
        Mockito.when(templateService.generateEmailContent(Mockito.any())).thenReturn(MESSAGE);
        Mockito.when(templateService.getEmailSubject(Mockito.any())).thenReturn("SUBJ");

        KafkaNotificationDto dto = Mockito.mock(KafkaNotificationDto.class);
        UserInfoDto user = Mockito.mock(UserInfoDto.class);
        Mockito.when(user.getTgChatId()).thenReturn(null);
        Mockito.when(user.getUsername()).thenReturn(EMAIL_TEST);
        Mockito.when(dto.getUserInfo()).thenReturn(user);

        var service = new DefaultNotificationServiceImpl(
                emailSenderService, telegramSenderService, templateService);

        service.notifyUser(dto);

        Mockito.verifyNoInteractions(telegramSenderService);
        Mockito.verify(emailSenderService, Mockito.atLeastOnce())
                .sendEmail(EMAIL_TEST, "SUBJ", MESSAGE);
    }
}
