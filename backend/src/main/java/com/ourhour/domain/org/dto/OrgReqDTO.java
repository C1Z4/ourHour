package com.ourhour.domain.org.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgReqDTO {

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String memberName;

    @NotBlank(message = "회사명은 필수입니다.")
    @Size(min = 2, max = 100, message = "이름은 2글자 이상입니다.")
    private String name;

    private String address;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String representativeName;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

    private String businessNumber;

    @Pattern(regexp = "^(https?://.*|data:image/(png|jpg|jpeg|gif|svg\\+xml);base64,.*)$", message = "올바른 URL 또는 Base64 이미지 데이터여야 합니다")
    private String logoImgUrl;

}
