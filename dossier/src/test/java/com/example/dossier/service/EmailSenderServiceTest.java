package com.example.dossier.service;

import com.example.dossier.model.EmailMessage;
import com.example.dossier.model.Theme;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    JavaMailSender mailSender;
    @InjectMocks
    EmailSenderService emailSenderService;

    @Test
    void sendEmail() {
        EmailMessage emailMessage = new EmailMessage("qwe@rty.ru",
                Theme.APPLICATION_DENIED,
                1L);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("qwe@rty.ru");
        message.setSubject(Theme.APPLICATION_DENIED.toString());
        message.setText("sometext");

        emailSenderService.sendEmail(emailMessage, "sometext");

        verify(mailSender, times(1)).send(message);
    }
}