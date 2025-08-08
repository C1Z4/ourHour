package com.ourhour.domain.user.dto;

import com.ourhour.global.common.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PwdChangeReqDTO {

    private String currentPassword;

    @ValidPassword
    private String newPassword;

    private String newPasswordCheck;

}
