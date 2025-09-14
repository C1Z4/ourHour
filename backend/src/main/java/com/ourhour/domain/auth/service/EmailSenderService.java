package com.ourhour.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromMail;

    public void sendEmail(String toEmail, String subject, String content) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(fromMail);

            mailSender.send(mimeMessage);

            System.out.println("이메일 발송 성공!");
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패",e);
        }
    }

    @Async("taskExecutor")
    public void sendEmailAsync(String toEmail, String subject, String content) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(fromMail);

            mailSender.send(mimeMessage);

            System.out.println("이메일 발송 성공!");
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패",e);
        }
    }

}
