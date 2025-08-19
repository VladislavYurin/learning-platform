package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.service.DefaultNotificationService;
import ru.mentor.service.EmailSenderService;
import ru.mentor.service.NotificationTemplateService;
import ru.mentor.service.TelegramSenderService;

/**
 * Реализация сервиса уведомлений.
 *
 * Отвечает за отправку уведомлений пользователям
 * через электронную почту и Telegram, используя заданные шаблоны уведомлений.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultNotificationServiceImpl implements DefaultNotificationService {

    private final EmailSenderService emailSenderService;

    private final TelegramSenderService telegramSenderService;

    private final NotificationTemplateService notificationTemplateService;

    /**
     * Уведомляет пользователя об уведомлении, отправляя его через email и Telegram.
     *
     * @param notificationDto объект, содержащий данные уведомления для отправки пользователю.
     */
    @Override
    public void notifyUser(KafkaNotificationDto notificationDto) {
        notifyByEmail(notificationDto);
        notifyByTelegram(notificationDto);
    }

    /**
     * Отправляет уведомление пользователю через электронную почту.
     *
     * @param notificationDto объект, содержащий данные уведомления.
     */
    private void notifyByEmail(KafkaNotificationDto notificationDto) {
        try {
            String emailContent = notificationTemplateService.generateEmailContent(notificationDto);
            String subject = notificationTemplateService.getEmailSubject(notificationDto.getNotificationType());

            emailSenderService.sendEmail(
                    notificationDto.getUserInfo().getUsername(),
                    subject,
                    emailContent
            );
        } catch (Exception ignored) {
            // Игнорируем ошибки отправки email для упрощения обработки
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

        // Если у пользователя есть Telegram чат, то отправляем уведомление в него
        if (userInfo.getTgChatId() != null) {
            telegramSenderService.sendMessage(userInfo.getTgChatId(), text, true);
            log.info("Сообщение отправлено в Телеграм чат айди = {}, имя пользователя = {}, айди пользователя = {}",
                    userInfo.getTgChatId(), userInfo.getUsername(), userInfo.getId());
        } else {
            // Если у пользователя нет Telegram чата, то отправляем уведомление по почте
            notifyByEmail(notificationDto);
            log.info("Сообщение отправлено по почте айди пользователя = {}, имя пользователя = {}",
                    userInfo.getId(), userInfo.getUsername());
        }
    }

}
