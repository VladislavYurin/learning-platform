package ru.mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.mentor.config.TelegramBotProperties;

/**
 * Главный класс сервиса уведомлений.
 * @author Vladislav Yurin
 */
@EnableConfigurationProperties(TelegramBotProperties.class)
@EnableFeignClients(basePackages = "ru.mentor.integration.telegram")
@SpringBootApplication
public class NotificationService {

    public static void main(String[] args) {
        SpringApplication.run(NotificationService.class, args);
    }

}