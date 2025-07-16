package com.ourhour.domain.user.util;

import com.ourhour.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.ourhour.domain.user.exception.UserException.notMatchPwd;

@Component
@RequiredArgsConstructor
public class PasswordChanger {

    private final PasswordEncoder passwordEncoder;

    public void changePassword(UserEntity userEntity, String newPwd, String newPwdCheck) {

        // 예외 상황: 새 비밀번호와 재확인 비밀번호 불일치
        if (!newPwd.equals(newPwdCheck)) {
            throw notMatchPwd();
        }

        // 비밀번호 변경
        String hashedPwd = passwordEncoder.encode(newPwd);
        userEntity.changePassword(hashedPwd);

    }

}
