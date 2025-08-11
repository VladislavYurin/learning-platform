package ru.mentor.service;

/**
 * Сервис для отправки сообщений в Telegram через Bot API.
 */
public interface TelegramSenderService {

    void sendMessage(Long chatId, String text, boolean html);
}
