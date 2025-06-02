package com.school.degreetopicsmanagement.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification for Degree Topics Management");

        String body =
                  "Thank you for registering with the Degree Topics Management System.\n"
                + "To complete your registration, please use the following verification code:\n\n"
                + code + "\n\n"
                + "If you did not request this code, please disregard this email.\n\n"
                + "Best regards,\n"
                + "Degree Topics Management Team";

        message.setText(body);
        mailSender.send(message);
    }

}
