package ru.mentor.service;

public interface EmailSenderService {

    void sendEmail(String toAddress, String subject, String message);

}
