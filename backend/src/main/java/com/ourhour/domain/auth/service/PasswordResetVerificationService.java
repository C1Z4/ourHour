package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.entity.PasswordResetVerificationEntity;
import com.ourhour.domain.auth.repository.PasswordResetVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service

public class PasswordResetVerificationService extends AbstractVerificationService<PasswordResetVerificationEntity> {

    private final PasswordResetVerificationRepository passwordResetVerificationRepository;

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    public PasswordResetVerificationService(EmailSenderService emailSenderService, PasswordResetVerificationRepository passwordResetVerificationRepository) {
        super(emailSenderService);
        this.passwordResetVerificationRepository = passwordResetVerificationRepository;
    }

    // 비밀번호 재설정을 위한 인증 링크 발송
    @Transactional
    public void sendEmailPwdResetVerificationLink(String email) {

        String subject = "[OURHOUR] 비밀번호 변경 안내";
        String contentTemplate = "<p>비밀번호 변경을 위해 아래 링크를 클릭해주세요.\n</p>";
        
        
        
        String linkName = "비밀번호 변경하기";

        sendVerificationEmail(email, serviceBaseUrl, "/api/auth/password-reset-verification?token=", subject, contentTemplate, linkName, passwordResetVerificationRepository);

    }

    // 비밀번호 재설정을 위한 이메일 인증하기
    @Transactional
    public void verifyPwdResetEmail(String token) {

        Optional<PasswordResetVerificationEntity> passwordResetVerificationEntity = passwordResetVerificationRepository.findByToken(token);

        verifyEmail(token, passwordResetVerificationEntity);

    }

    @Override
    protected PasswordResetVerificationEntity buildVerificationEntity(String token, String email, LocalDateTime createdAt, LocalDateTime expiredAt, boolean isUsed) {
        return PasswordResetVerificationEntity.builder()
                .token(token)
                .email(email)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .isUsed(false)
                .build();
    }
}
