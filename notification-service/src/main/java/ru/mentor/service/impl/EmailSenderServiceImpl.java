package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import ru.mentor.service.EmailSenderService;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "application.notify.email.enable", havingValue = "true")
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender emailSender = new JavaMailSenderImpl();

    @Override
    public void sendEmail(String toAddress, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

}
