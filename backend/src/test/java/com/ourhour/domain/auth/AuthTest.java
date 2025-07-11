package com.ourhour.domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class AuthTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("비밀번호 해시화 테스트")
    @Test
    void encodePwd() {
        // given
        String rawPassword = "ourhour1234";

        // when
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println(rawPassword);
        System.out.println(encodedPassword);

        // then
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword); // 해싱 값이 원래 비번과 다름
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue(); // 검증 성공
    }
}
