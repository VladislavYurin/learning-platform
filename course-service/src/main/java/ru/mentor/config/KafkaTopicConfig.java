package ru.mentor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/**
 * Настройка автосоздания топиков в Kafka.
 * Создаёт notification-topic при старте приложения.
 */
@Configuration
public class KafkaTopicConfig {
    /**
     * Регистрирует бин описания топика
     * @return описание топика для создания брокером
     */
    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name("notification-topic").build();
    }
}
