package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import ru.mentor.service.EmailSenderService;

/**
 * Реализация сервиса отправки электронных писем.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender emailSender = new JavaMailSenderImpl();

    /**
     * Отправляет электронное письмо на указанный адрес.
     *
     * @param toAddress адрес электронной почты получателя.
     * @param subject   тема электронного письма.
     * @param message   содержание электронного письма.
     */
    @Override
    public void sendEmail(String toAddress, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
        // Логирование отправленного письма
        log.info("Email отправлен: {}", toAddress);
    }

}
