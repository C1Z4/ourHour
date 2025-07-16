package com.ourhour.domain.user.service;

import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.auth.service.EmailVerificationService;
import com.ourhour.domain.user.dto.PwdChangeReqDTO;
import com.ourhour.domain.user.dto.PwdVerifyReqDTO;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.domain.user.util.PasswordChanger;
import com.ourhour.domain.user.util.PasswordVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ourhour.domain.user.exception.UserException.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordVerifier passwordVerifier;
    private final PasswordChanger passwordChanger;
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    // 비밀번호 변경
    @Transactional
    public void changePwd(PwdChangeReqDTO pwdChangeReqDTO) {

        String currentPwd = pwdChangeReqDTO.getCurrentPassword();
        String newPwd = pwdChangeReqDTO.getNewPassword();
        String newPwdCheck = pwdChangeReqDTO.getNewPasswordCheck();

        UserEntity userEntity = passwordVerifier.verifyPassword(currentPwd);

        // 예외 발생: 기존 비밀번호와 새 비밀번호 일치
        if (newPwd.equals(currentPwd)) {
            throw samePwd();
        }

        passwordChanger.changePassword(userEntity, newPwd, newPwdCheck);

    }

    // 비밀번호 확인
    @Transactional
    public void verifyPwd(PwdVerifyReqDTO pwdVerifyReqDTO) {

        String pwd = pwdVerifyReqDTO.getPassword();

        passwordVerifier.verifyPassword(pwd);

    }

    // 계정 탈퇴
    @Transactional
    public void deleteUser(PwdVerifyReqDTO pwdVerifyReqDTO) {

        String pwd = pwdVerifyReqDTO.getPassword();

        UserEntity userEntity = passwordVerifier.verifyPassword(pwd);

        // soft delete 처리
        userEntity.markAsDeleted();

        // 인증 이메일 무효화
        emailVerificationRepository.invalidateByEmail(userEntity.getEmail());
    }
}
