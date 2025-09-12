package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.mentor.config.TelegramBotProperties;
import ru.mentor.integration.telegram.TelegramApiClient;
import ru.mentor.service.TelegramSenderService;

/**
 * Реализация {@link TelegramSenderService} с использованием OpenFeign и Telegram Bot API.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "application.notify.telegram.enable", havingValue = "true")
public class TelegramSenderServiceImpl implements TelegramSenderService {

    private final TelegramBotProperties botProperties;

    private final TelegramApiClient telegramApiClient;

    /**
     * Отправляет сообщение в Telegram.
     *
     * Если chatId равен null, отправка сообщения отменяется.
     *
     * @param chatId ID чата получателя. Если null — отправка отменяется.
     * @param text   текст сообщения, которое будет отправлено.
     * @param html   флаг, указывающий, требуется ли обрабатывать текст как HTML.
     */
    @Override
    public void sendMessage(Long chatId, String text, boolean html) {
        telegramApiClient.sendMessage(botProperties.token(), chatId, text, true);
    }
}
