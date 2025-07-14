package com.ourhour.domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PwdTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("비밀번호 해시화 테스트")
    @Test
     void encodePwd() {
        // given
        String rawPassword = "ourhour1234";

        // when
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // then
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
