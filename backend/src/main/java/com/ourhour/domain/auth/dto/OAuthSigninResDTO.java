package com.ourhour.domain.auth.dto;

import com.ourhour.domain.user.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSigninResDTO {

    private boolean isNewUser;
    private String email;
    private String oauthId;
    private Platform platform;
    private String accessToken;
    private String refreshToken;

}
