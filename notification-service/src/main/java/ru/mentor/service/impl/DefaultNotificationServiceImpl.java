package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.service.DefaultNotificationService;
import ru.mentor.service.EmailSenderService;
import ru.mentor.service.NotificationTemplateService;
import ru.mentor.service.TelegramSenderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultNotificationServiceImpl implements DefaultNotificationService {

    private final ObjectProvider<EmailSenderService> emailSenderServiceProvider;
    private final ObjectProvider<TelegramSenderService> telegramSenderServiceProvider;
    private final NotificationTemplateService notificationTemplateService;

    @Override
    public void notifyUser(KafkaNotificationDto notificationDto) {
        notifyByEmail(notificationDto);
        notifyByTelegram(notificationDto);
    }

    private void notifyByEmail(KafkaNotificationDto notificationDto) {
        try {
            EmailSenderService emailSenderService = emailSenderServiceProvider.getIfAvailable();

            if (emailSenderService == null) {
                log.error("Не удалось отправить сообщение по почте");
                return;
            }

            String emailContent = notificationTemplateService.generateEmailContent(notificationDto);
            String subject = notificationTemplateService.getEmailSubject(notificationDto.getNotificationType());

            emailSenderService.sendEmail(
                    notificationDto.getUserInfo().getUsername(),
                    subject,
                    emailContent
            );
        } catch (Exception ignored) {
        }
    }

    /**
     * Формирует текст уведомления и отправляет его в Telegram.
     *
     * @param notificationDto данные уведомления
     */
    private void notifyByTelegram(KafkaNotificationDto notificationDto) {
        String text = notificationTemplateService.generateEmailContent(notificationDto);
        UserInfoDto userInfo = notificationDto.getUserInfo();
        TelegramSenderService telegramSenderService = telegramSenderServiceProvider.getIfAvailable();

        if (userInfo.getTgChatId() != null && telegramSenderService != null) {
            telegramSenderServiceProvider.getIfAvailable().sendMessage(userInfo.getTgChatId(), text, true);
            log.info("Сообщение отправлено в Телеграм чат айди = {}, имя пользователя = {}, айди пользователя = {}",
                    userInfo.getTgChatId(), userInfo.getUsername(), userInfo.getId());
        } else {
            notifyByEmail(notificationDto);
            log.info("Сообщение отправлено по почте айди пользователя = {}, имя пользователя = {}",
                    userInfo.getId(), userInfo.getUsername());
        }
    }

}
