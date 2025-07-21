package com.ourhour.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyMemberInfoReqDTO {

    private String name;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^(https?://.*|data:image/(png|jpg|jpeg|gif|svg\\+xml);base64,.*|/images/.*)$", message = "올바른 URL, Base64 이미지 데이터, 또는 기존 이미지 경로여야 합니다")
    private String profileImgUrl;

    private String deptName;
    private String positionName;

}
