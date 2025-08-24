package ru.mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.mentor.config.TelegramBotProperties;

@EnableConfigurationProperties(TelegramBotProperties.class)
@EnableFeignClients(basePackages = "ru.mentor.integration.telegram")
@SpringBootApplication
@EnableScheduling
public class NotificationService {

    public static void main(String[] args) {
        SpringApplication.run(NotificationService.class, args);
    }

}