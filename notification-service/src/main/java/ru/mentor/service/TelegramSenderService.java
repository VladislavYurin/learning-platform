package ru.mentor.service;

/**
 * Интерфейс для сервиса отправки сообщений в Telegram.
 * Этот интерфейс определяет метод для отправки текстовых сообщений в чаты Telegram.
 */
public interface TelegramSenderService {

    /**
     * Отправляет сообщение в указанный чат Telegram.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение.
     * @param text   текст сообщения, которое нужно отправить.
     * @param html   флаг, указывающий, нужно ли обрабатывать текст как HTML.
     */
    void sendMessage(Long chatId, String text, boolean html);
}
