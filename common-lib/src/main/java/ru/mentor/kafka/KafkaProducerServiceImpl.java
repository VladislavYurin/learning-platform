package ru.mentor.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.mentor.dto.kafka.KafkaNotificationDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, KafkaNotificationDto> kafkaTemplate;

    @Override
    public void send(KafkaNotificationDto notificationDto) {
        try {
            kafkaTemplate
                    .send("notification-topic", UUID.randomUUID().toString(), notificationDto)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error(
                                    "Сообщение не отправлено в кафку. Произошла ошибка",
                                    exception
                            );
                        } else {
                            log.info(
                                    "Сообщение в кафку успешно отправлено: {}",
                                    result.getRecordMetadata()
                            );
                        }
                    }).get();
        } catch (InterruptedException | ExecutionException ignored) {
            log.error("Ошибка при отправке сообщения в кафку");
        }
    }

}
