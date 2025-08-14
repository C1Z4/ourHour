package com.ourhour.domain.auth.dto;

import com.ourhour.global.common.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PwdResetReqDTO {

    private String token;

    @ValidPassword
    private String newPassword;

    private String newPasswordCheck;
}
