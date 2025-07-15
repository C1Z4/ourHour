package com.ourhour.global.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            // 기본 메시지 비활성화
            context.disableDefaultConstraintViolation();
            // 커스텀 메시지 생성
            context.buildConstraintViolationWithTemplate("비밀번호는 필수 입력값입니다.").addConstraintViolation();
        }

        // 패턴 검사
        boolean isMatched = value.matches("^(?=.*[a-z])(?=.*[0-9]).{8,64}$");
        if (!isMatched) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호는 소문자와 숫자를 반드시 포함하여 최소 8자 이상이어야 합니다.").addConstraintViolation();
        }

        return isMatched;
    }
}
