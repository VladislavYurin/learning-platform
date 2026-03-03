package ru.mentor.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailSenderServiceImpl emailSenderService;

    private static final String TO_ADDRESS = "пользователь@example.com";
    private static final String SUBJECT = "Тестовая тема";
    private static final String MESSAGE = "Сообщение";

    @Test
    void sendEmail_sendsCorrectMessage(){
        emailSenderService.sendEmail(TO_ADDRESS, SUBJECT, MESSAGE);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertNotNull(sent);
        assertArrayEquals(new String[]{TO_ADDRESS}, sent.getTo());
        assertEquals(SUBJECT, sent.getSubject());
        assertEquals(MESSAGE, sent.getText());
    }
}
