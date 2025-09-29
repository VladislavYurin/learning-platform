package ru.mentor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Конфигурация для напоминаний о встречах
 */
@Data
@Component
@ConfigurationProperties(prefix = "reminder")
public class ReminderProperties {

    /**
     * Периодичность запроса слотов из базы данных
     */
    private Long schedulerIntervalMs = 300000L;

    /**
     * За сколько минут до начала встречи нужно отправлять напоминания
     */
    private Integer remindBeforeMinutes = 30;

}
