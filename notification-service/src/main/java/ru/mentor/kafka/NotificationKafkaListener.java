package ru.mentor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.service.DefaultNotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "notification-topic", groupId = "notification-event", containerFactory = "kafkaListenerContainerFactory")
public class NotificationKafkaListener {

    private final DefaultNotificationService defaultNotificationService;

    @KafkaHandler
    public void processNotification(KafkaNotificationDto notificationDto) {
        defaultNotificationService.notifyUser(notificationDto);
    }

}
