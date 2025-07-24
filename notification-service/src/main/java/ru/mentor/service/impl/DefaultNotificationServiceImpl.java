package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.service.DefaultNotificationService;
import ru.mentor.service.EmailSenderService;
import ru.mentor.service.NotificationTemplateService;

@Service
@RequiredArgsConstructor

public class DefaultNotificationServiceImpl implements DefaultNotificationService {

    private final EmailSenderService emailSenderService;

    private final NotificationTemplateService notificationTemplateService;

    @Override
    public void notifyUser(KafkaNotificationDto notificationDto) {
        notifyByEmail(notificationDto);
        notifyByTelegram(notificationDto);
    }

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
        }
    }

    private void notifyByTelegram(KafkaNotificationDto notificationDto) {

    }

}
