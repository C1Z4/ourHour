package com.ourhour.global.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        // null 또는 빈 값 체크
        if (value == null || value.isEmpty()) {
            context.buildConstraintViolationWithTemplate("비밀번호는 필수 입력값입니다.")
                   .addConstraintViolation();
            return false;
        }

        // 패턴 검사
        boolean isMatched = value.matches("^(?=.*[a-z])(?=.*[0-9]).{8,64}$");
        if (!isMatched) {
            context.buildConstraintViolationWithTemplate("비밀번호는 소문자와 숫자를 반드시 포함하여 최소 8자 이상이어야 합니다.")
                   .addConstraintViolation();
        }

        return isMatched;
    }
}
