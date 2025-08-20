package com.ourhour.domain.user.util;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.ourhour.domain.user.exception.UserException.passwordNotMatch;

@Component
@RequiredArgsConstructor
public class PasswordVerifier {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserEntity verifyPassword(String currentPwd) {

        // 사용자 조회
        Long userId = SecurityUtil.getCurrentUserId();

        UserEntity userEntity = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(AuthException::userNotFoundException);

        // 예외 발생: 현재 비밀번호 불일치
        boolean isMatched = passwordEncoder.matches(currentPwd, userEntity.getPassword());
        if (!isMatched) {
            throw passwordNotMatch();
        }

        return userEntity;

    }

}
