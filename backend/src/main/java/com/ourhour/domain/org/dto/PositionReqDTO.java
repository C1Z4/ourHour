package com.ourhour.domain.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PositionReqDTO {
    
    @NotBlank(message = "직책명은 필수입니다.")
    @Size(max = 50, message = "직책명은 50자 이하여야 합니다.")
    private String name;
}