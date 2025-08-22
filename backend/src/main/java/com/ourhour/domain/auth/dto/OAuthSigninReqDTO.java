package com.ourhour.domain.auth.dto;

import com.ourhour.domain.user.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSigninReqDTO {

    private String code;
    private Platform platform;

}
