package ru.mentor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные свойства для Telegram-бота.
 * @param token токен API для доступа к вашему Telegram-боту.
 * @param apiUrl URL-адрес API Telegram.
 */
@ConfigurationProperties(prefix = "telegram.bot")
public record TelegramBotProperties(
        String token,
        String apiUrl
) {
}
