package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.entity.NotificationEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.NotificationSendException;
import ru.mentor.repository.UserRepository;
import ru.mentor.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private boolean mailSent = false;
    private boolean telegramSent = false;
    public Exception lastException = null;

    @Override
    public void notifyUser(KafkaNotificationDto notificationDto) {
        try {
            notifyByEmail(notificationDto);
        }catch (Exception e){
            lastException = e;
        }

        try {
            notifyByTelegram(notificationDto);
        }catch (Exception e){
            lastException = e;
        }

        saveNotification(notificationDto, lastException);
    }

    private void notifyByEmail(KafkaNotificationDto notificationDto) {

        EmailSenderService emailSenderService = emailSenderServiceProvider.getIfAvailable();

        if (emailSenderService == null) {
            log.error("Не удалось отправить сообщение по почте = {}",
                    notificationDto.getUserInfo().getUsername());
            throw new NotificationSendException("Ошибка при отправке сообщения по почте");
        }

        String emailContent = notificationTemplateService.generateEmailContent(notificationDto);
        String subject = notificationTemplateService.getEmailSubject(notificationDto.getNotificationType());

        emailSenderService.sendEmail(
                notificationDto.getUserInfo().getUsername(),
                subject,
                emailContent
        );

        log.info("Сообщение отправлено по почте = {}",
                notificationDto.getUserInfo().getUsername());

        mailSent = true;

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

        if (userInfo.getTgChatId() == null && telegramSenderService == null) {
            log.error("Не удалось отправить сообщение в Телеграм чат айди = {}, имя пользователя = {}, айди пользователя = {}",
                    userInfo.getTgChatId(), userInfo.getUsername(), userInfo.getId());
            throw new NotificationSendException("Ошибка при отправке сообщения в Телеграм чат");
        }

        telegramSenderServiceProvider.getIfAvailable().sendMessage(userInfo.getTgChatId(), text, true);
        telegramSent = true;
        log.info("Сообщение отправлено в Телеграм чат айди = {}, имя пользователя = {}, айди пользователя = {}",
                userInfo.getTgChatId(), userInfo.getUsername(), userInfo.getId());
    }

    /**
     * Сохраняет успешно отправленное уведомление в базе данных.
     *
     * @param notificationDto данные уведомления
     */
    private void saveNotification(KafkaNotificationDto notificationDto, Exception lastException) {

        NotificationEntity notification = new NotificationEntity().builder()
                .notificationType(notificationDto.getNotificationType())
                .recipient(getUserEntityFromNotificationDto(notificationDto))
                .notificationStatus(getNotificationStatus())
                .build();

        if (lastException != null) {
            notification.setErrorText(lastException.getMessage());
        }

        notificationRepository.save(notification);
    }

    /**
     * Возвращает статус уведомления.
     */
    private NotificationStatus getNotificationStatus() {
        if (true == mailSent && telegramSent) {
            return NotificationStatus.SENT_BOTH;
        } else if (true == mailSent) {
            return NotificationStatus.SENT_MAIL;
        } else if (true == telegramSent) {
            return NotificationStatus.SENT_TG;
        } else {
            return NotificationStatus.ERROR_SENT;
        }
    }

    /**
     * Возвращает сущность пользователя-адрессата.
     */
    private UserEntity getUserEntityFromNotificationDto(KafkaNotificationDto notificationDto) {
        return userRepository.findByUsernameOrThrow(notificationDto.getUserInfo().getUsername());
    }

}
