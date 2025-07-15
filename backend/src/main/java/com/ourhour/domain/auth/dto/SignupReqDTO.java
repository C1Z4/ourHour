package com.ourhour.domain.auth.dto;

import com.ourhour.global.common.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDTO {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email
    private String email;

    @ValidPassword
    private String password;
}
