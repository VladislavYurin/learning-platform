package ru.mentor.service;

/**
 * Интерфейс для сервиса отправки электронных писем.
 */
public interface EmailSenderService {

    /**
     * Отправляет электронное письмо на указанный адрес.
     *
     * @param toAddress адрес электронной почты получателя.
     * @param subject   тема электронного письма.
     * @param message   содержание электронного письма.
     */
    void sendEmail(String toAddress, String subject, String message);

}
