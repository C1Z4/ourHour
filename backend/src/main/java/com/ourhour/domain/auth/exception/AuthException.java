package com.ourhour.domain.auth.exception;

import com.ourhour.global.exception.BusinessException;

public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(int status, String message) {
        super(status, message);
    }

    public static AuthException duplicateRequestException() {
        return new AuthException(400, "이미 존재하는 이메일입니다.");
    }

    public static AuthException emailNotFoundException() {
        return new AuthException(404, "이메일이 존재하지 않습니다.");
    }

    public static AuthException invalidPasswordException() {
        return new AuthException(401, "비밀번호가 일치하지 않습니다.");
    }

    public static AuthException invalidTokenException() {
        return new AuthException(401, "토큰이 유효하지 않습니다.");
    }

    public static AuthException tokenNotFoundException() {
        return new AuthException(404, "토큰이 존재하지 않습니다.");
    }

    public static AuthException userNotFoundException() {
        return new AuthException(404, "해당 유저가 존재하지 않습니다.");
    }

    public static AuthException emailVerificationException(String message) {
        return new AuthException(message);
    }

}
