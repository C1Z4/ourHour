package com.ourhour.domain.auth.dto;

import com.ourhour.domain.user.enums.Platform;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthExtraInfoReqDTO {

    private String oauthId;
    private Platform platform;
    @Email
    private String email;
    private String password;


}
