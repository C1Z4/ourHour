package com.ourhour.domain.auth.exception;

import com.ourhour.global.exception.BusinessException;

public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(message);
    }

    public static AuthException duplicateRequestException() {
        return new AuthException("이미 존재하는 이메일입니다.");
    }

    public static AuthException emailVerificationException(String message) {
        return new AuthException(message);
    }
}
