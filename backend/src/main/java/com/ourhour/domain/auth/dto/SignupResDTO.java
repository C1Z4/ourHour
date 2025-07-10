package com.ourhour.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResDTO {

    private Long userId;
    private String email;
    private String platform;
    private String accessToken;
}
