package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.PwdResetReqDTO;
import com.ourhour.domain.auth.entity.PasswordResetVerificationEntity;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.repository.PasswordResetVerificationRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.domain.user.util.PasswordChanger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ourhour.domain.auth.exception.AuthException.unauthorizedException;

@Service

public class PasswordResetService extends AbstractVerificationService<PasswordResetVerificationEntity> {

    private final PasswordResetVerificationRepository passwordResetVerificationRepository;
    private final PasswordChanger passwordChanger;
    private final UserRepository userRepository;

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    public PasswordResetService(EmailSenderService emailSenderService, PasswordResetVerificationRepository passwordResetVerificationRepository, PasswordChanger passwordChanger, UserRepository userRepository) {
        super(emailSenderService);
        this.passwordResetVerificationRepository = passwordResetVerificationRepository;
        this.passwordChanger = passwordChanger;
        this.userRepository = userRepository;
    }

    // 비밀번호 재설정을 위한 인증 링크 발송
    @Transactional
    public void sendEmailPwdResetVerificationLink(String email) {

        String subject = "[OURHOUR] 비밀번호 변경 안내";
        String contentTemplate = "<p>비밀번호 변경을 위해 아래 링크를 클릭해주세요.\n</p>";
        
        
        
        String linkName = "비밀번호 변경하기";

        sendVerificationEmail(email, serviceBaseUrl, "/api/auth/password-reset/verification?token=", subject, contentTemplate, linkName, passwordResetVerificationRepository);

    }

    // 비밀번호 재설정을 위한 이메일 인증하기
    @Transactional
    public void verifyPwdResetEmail(String token) {

        Optional<PasswordResetVerificationEntity> passwordResetVerificationEntity = passwordResetVerificationRepository.findByToken(token);

        verifyEmail(token, passwordResetVerificationEntity);

    }

    // 비밀번호 재설정
    @Transactional
    public void resetPwd(PwdResetReqDTO pwdResetReqDTO) {

        String token = pwdResetReqDTO.getToken();
        String newPwd = pwdResetReqDTO.getNewPassword();
        String newPwdCheck = pwdResetReqDTO.getNewPasswordCheck();

        Optional<PasswordResetVerificationEntity> passwordResetVerificationEntity = passwordResetVerificationRepository.findByToken(token);

        // 예외 발생: 인증되지 않은 사용자가 재설정 하려는 경우
        if (!passwordResetVerificationEntity.get().isUsed()) {
            throw unauthorizedException();
        }

        // 인증된 사용자의 이메일을 통해 계정 조회
        String userEmail = passwordResetVerificationEntity.get().getEmail();
        UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(userEmail)
                .orElseThrow(AuthException::userNotFoundException);

        // 비밀번호 재설정
        passwordChanger.changePassword(userEntity, newPwd, newPwdCheck);

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
