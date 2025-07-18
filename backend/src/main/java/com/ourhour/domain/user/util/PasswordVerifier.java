package com.ourhour.domain.user.util;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.ourhour.domain.auth.exception.AuthException.unauthorizedException;
import static com.ourhour.domain.user.exception.UserException.invalidPwd;

@Component
@RequiredArgsConstructor
public class PasswordVerifier {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserEntity verifyPassword(String currentPwd) {

        // 사용자 조회
        Claims claims = UserContextHolder.get();
        Long userId = claims.getUserId();

        UserEntity userEntity = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(AuthException::userNotFoundException);

        // 예외 발생: 현재 비밀번호 불일치
        boolean isMatched = passwordEncoder.matches(currentPwd, userEntity.getPassword());
        if (!isMatched) {
            throw invalidPwd();
        }

        return userEntity;

    }

}
