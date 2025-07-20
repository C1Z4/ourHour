package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.entity.AbstractVerificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.ourhour.domain.auth.exception.AuthException.emailVerificationException;

@RequiredArgsConstructor
public abstract class AbstractVerificationService<T extends AbstractVerificationEntity> {

    private final EmailSenderService emailSenderService;
    private static final String EMAIL_TEMPLATE = """
    <p>안녕하세요. OURHOUR입니다.</p><br/>
    %s
    <p>이 링크는 15분 동안 유효합니다.</p>
    <br/><p>감사합니다.<br/>OURHOUR 팀 드림</p>
    """;

    protected String sendVerificationEmail(
            String email,
            String serviceBaseUrl,
            String endpoint,
            String subject,
            String contentTemplate,
            String linkName
    ) {

        // 토큰 생성
        String token = UUID.randomUUID().toString();

        // 이메일 내용 구성
        String link = serviceBaseUrl + endpoint + token;
        String bodyContent = String.format("""
            %s
            <p><a href="%s" style="color:#1a73e8; text-decoration:none;"><strong>%s</strong></a></p>
            """, contentTemplate, link, linkName);
        String content = String.format(EMAIL_TEMPLATE, bodyContent);

        emailSenderService.sendEmail(email, subject, content);

        return token;

    }

    protected void verifyEmail(
            String token,
            Optional<T> entityOptional
    ) {

        // 토큰 유효성 검사
        // 1. 토큰 조회
        T entity = entityOptional.orElseThrow(() -> emailVerificationException("유효하지 않은 초대링크입니다."));

        // 2. 만료 시간 확인
        if (entity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw emailVerificationException("이메일 인증 링크가 만료되었습니다.");
        }

        // 3. 토큰 사용 여부 확인 (default(인증 안됐을 시) : isUsed = false)
        if(Boolean.TRUE.equals(entity.isUsed())) {
            throw emailVerificationException("이미 인증된 이메일 인증 링크 입니다.");
        }

        // 4. 토큰 유효 처리
        entity.setUsed(true);

    }

    protected abstract T buildVerificationEntity(String token, String email, LocalDateTime createdAt, LocalDateTime expiredAt, boolean isUsed);
}
