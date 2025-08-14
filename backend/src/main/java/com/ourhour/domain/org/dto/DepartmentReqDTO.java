package com.ourhour.domain.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepartmentReqDTO {
    
    @NotBlank(message = "부서명은 필수입니다.")
    @Size(max = 50, message = "부서명은 50자 이하여야 합니다.")
    private String name;
}