package com.example.dossier.service;

import com.example.dossier.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public void sendEmail(EmailMessage emailMessage, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailMessage.getAddress());
        message.setSubject(emailMessage.getTheme().toString());
        message.setText(text);
        log.info("отправляется e-mail сообщение {}", message);
        mailSender.send(message);
    }
}
