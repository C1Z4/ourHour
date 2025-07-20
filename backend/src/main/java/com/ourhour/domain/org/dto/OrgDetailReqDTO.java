package com.ourhour.domain.org.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgDetailReqDTO {

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

    @Pattern(
            regexp = "^(https?://.*|data:image/(png|jpg|jpeg|gif|svg\\+xml);base64,.*|/images/.*)$",
            message = "올바른 URL, Base64 이미지 데이터, 또는 기존 이미지 경로여야 합니다"
    )
    private String logoImgUrl;

}
