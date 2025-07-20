package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.entity.EmailVerificationEntity;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailVerificationService extends AbstractVerificationService<EmailVerificationEntity>{

    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    public EmailVerificationService(EmailSenderService emailSenderService, EmailVerificationRepository emailVerificationRepository) {
        super(emailSenderService);
        this.emailVerificationRepository = emailVerificationRepository;
    }

    // 이메일 인증 링크 발송
    @Transactional
    public void sendEmailVerificationLink(String email) {

        String subject = "[OURHOUR] 이메일 인증 안내";
        String contentTemplate = "<p>OURHOUR 서비스 이용을 위해 아래 링크를 클릭하여 인증을 완료해주세요.\n</p>";
        String linkName = "이메일 인증하기";

        String token = sendVerificationEmail(
                email,
                serviceBaseUrl,
                "/api/auth/email-verification?token=",
                subject,
                contentTemplate,
                linkName);

        // DB 저장
        EmailVerificationEntity emailVerificationEntity = buildVerificationEntity(
                token, email, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), false
        );
        emailVerificationRepository.save(emailVerificationEntity);

    }

    // 이메일 인증하기
    @Transactional
    public void verifyEmail(String token) {

        Optional<EmailVerificationEntity> emailVerificationEntity = emailVerificationRepository.findByToken(token);

        verifyEmail(token, emailVerificationEntity);

    }

    @Override
    protected EmailVerificationEntity buildVerificationEntity(String token, String email, LocalDateTime createdAt, LocalDateTime expiredAt, boolean isUsed) {
        return EmailVerificationEntity.builder()
                .token(token)
                .email(email)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .isUsed(isUsed)
                .build();
    }
}
