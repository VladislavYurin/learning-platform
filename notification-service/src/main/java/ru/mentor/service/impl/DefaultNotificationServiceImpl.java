package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import ru.mentor.constant.NotificationDestination;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.entity.NotificationEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.NotificationSendException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.KafkaMapper;
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
    private final BaseMapper baseMapper;
    private final KafkaMapper kafkaMapper;

    @Override
    public void notifyUser(KafkaNotificationDto notificationDto) {
        notifyByEmail(notificationDto);
        notifyByTelegram(notificationDto);
    }

    private void notifyByEmail(KafkaNotificationDto notificationDto) {

        try {
            EmailSenderService emailSenderService = emailSenderServiceProvider.getIfAvailable();

            if (emailSenderService == null) {
                throw new NotificationSendException("Ошибка при отправке сообщения по почте");
            }

            String emailContent = notificationTemplateService.generateEmailContent(notificationDto);
            String subject = notificationTemplateService.getEmailSubject(notificationDto.getNotificationType());

            emailSenderService.sendEmail(
                    notificationDto.getUserInfo().getUsername(),
                    subject,
                    emailContent
            );

            saveNotification(notificationDto, NotificationDestination.MAIL);

            log.info("Сообщение отправлено по почте = {}",
                    notificationDto.getUserInfo().getUsername());

        } catch (Exception e) {

            log.error("Не удалось отправить сообщение по почте = {}",
                    notificationDto.getUserInfo().getUsername());
            saveNotificationWithError(notificationDto, NotificationDestination.MAIL, e.getMessage());

        }
    }

    /**
     * Формирует текст уведомления и отправляет его в Telegram.
     *
     * @param notificationDto данные уведомления
     */
    private void notifyByTelegram(KafkaNotificationDto notificationDto) {
        try {
            String text = notificationTemplateService.generateEmailContent(notificationDto);
            UserInfoDto userInfo = notificationDto.getUserInfo();
            TelegramSenderService telegramSenderService = telegramSenderServiceProvider.getIfAvailable();

            if (userInfo.getTgChatId() == null || telegramSenderService == null) {
                throw new NotificationSendException("Ошибка при отправке сообщения в Телеграм чат");
            }

            telegramSenderServiceProvider.getIfAvailable().sendMessage(userInfo.getTgChatId(), text, true);

            saveNotification(notificationDto, NotificationDestination.TELEGRAM);

            log.info("Сообщение отправлено в Телеграм чат айди = {}, имя пользователя = {}, айди пользователя = {}",
                    userInfo.getTgChatId(), userInfo.getUsername(), userInfo.getId());

        }  catch (Exception e) {

            log.error("Не удалось отправить сообщение в Телеграм чат");
            saveNotificationWithError(notificationDto, NotificationDestination.TELEGRAM, e.getMessage());

        }
    }

    /**
     * Сохраняет успешно отправленное уведомление в базе данных.
     *
     * @param notificationDto данные уведомления
     * @param notificationDestination назначение уведомления
     */
    private void saveNotification(KafkaNotificationDto notificationDto, NotificationDestination notificationDestination) {

        NotificationEntity notification = kafkaMapper.mapNotificationEntity(
                                                      notificationDto,
                                                      notificationDestination,
                                                      null,
                                                      NotificationStatus.OK,
                                                      getUserEntityFromNotificationDto(notificationDto));

        notificationRepository.save(notification);
    }

    /**
     * Сохраняет не отправленное уведомление с сообщением ошибки в базе данных.
     *
     * @param notificationDto данные уведомления
     * @param notificationDestination назначение уведомления
     * @param exceptionMessage сообщение об ошибке
     */
    private void saveNotificationWithError(KafkaNotificationDto notificationDto, NotificationDestination notificationDestination, String exceptionMessage) {

        NotificationEntity notification = kafkaMapper.mapNotificationEntity(
                notificationDto,
                notificationDestination,
                exceptionMessage,
                NotificationStatus.ERROR,
                getUserEntityFromNotificationDto(notificationDto));

        notificationRepository.save(notification);
    }

    /**
     * Возвращает сущность пользователя-адрессата.
     */
    private UserEntity getUserEntityFromNotificationDto(KafkaNotificationDto notificationDto) {
        return baseMapper.mapUserEntity(notificationDto.getUserInfo());
    }

}
