package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.entity.EmailVerificationEntity;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ourhour.domain.auth.exception.AuthException.emailVerificationException;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailSenderService emailSenderService;

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    // 이메일 인증 링크 발송
    @Transactional
    public void sendEmailVerificationLink(String email) {

        // 토큰 생성
        String token = UUID.randomUUID().toString();

        // 현재 시각
        LocalDateTime now = LocalDateTime.now();

        // DB 저장
        EmailVerificationEntity emailVerificationEntity = EmailVerificationEntity.builder()
                .token(token)
                .createdAt(now)
                .expiredAt(now.plusMinutes(15))
                .isUsed(false)
                .email(email)
                .build();

        emailVerificationRepository.save(emailVerificationEntity);

        // 이메일 내용 구성
        String verificationLink = serviceBaseUrl + "/api/auth/email-verification?token=" + token;
        String emailSubject = "[OURHOUR] 이메일 주소 인증 안내"; // 이메일 제목
        String emailContent = "<p>안녕하세요. OURHOUR입니다.</p><br/>"
                + "<p>OURHOUR 서비스 이용을 위해 아래 링크를 클릭하여 인증을 완료해주세요.</p>"
                + "<p><a href=\"" + verificationLink + "\" style=\"color:#1a73e8; text-decoration:none;\"><strong>이메일 인증하기</strong></a></p>"
                + "<p>이 링크는 1시간 동안 유효합니다.</p>"
                + "<br/><p>감사합니다.<br/>OURHOUR 팀 드림</p>";

        // SMTP 서버에 메일 발송
        emailSenderService.sendEmail(email,emailSubject, emailContent);

    }

    // 이메일 인증하기
    @Transactional
    public boolean verifyEmail(String token) {

        // 토큰 유효성 검사
        // 1. 토큰 조회
        EmailVerificationEntity emailVerificationEntity = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> emailVerificationException("이메일 인증에 실패했습니다."));

        // 2. 만료 시간 확인
        if (emailVerificationEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw emailVerificationException("이메일 인증 링크가 만료되었습니다.");
        }

        // 3. 토큰 사용 여부 확인 (default : false)
        if(emailVerificationEntity.getIsUsed()) {
            throw emailVerificationException("이미 인증된 이메일 인증 링크 입니다.");
        }

        // 4. 토큰 유효 처리(Dirty Checking)
        emailVerificationEntity.setIsUsed(true);

        return emailVerificationEntity.getIsUsed();
    }
}
