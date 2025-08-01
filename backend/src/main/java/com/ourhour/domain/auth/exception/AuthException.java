package com.ourhour.domain.auth.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static AuthException unauthorizedException() {
        return new AuthException(ErrorCode.UNAUTHORIZED);
    }

    public static AuthException duplicateRequestException() {
        return new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    public static AuthException emailNotFoundException() {
        return new AuthException(ErrorCode.EMAIL_NOT_FOUND);
    }

    public static AuthException invalidPasswordException() {
        return new AuthException(ErrorCode.INVALID_PASSWORD);
    }

    public static AuthException tokenNotFoundException() {
        return new AuthException(ErrorCode.TOKEN_NOT_FOUND);
    }

    public static AuthException invalidTokenException() {
        return new AuthException(ErrorCode.INVALID_TOKEN);
    }

    public static AuthException deactivatedAccountException() {
        return new AuthException(ErrorCode.DEACTIVATED_ACCOUNT);
    }

    public static AuthException emailSendFailedException() {
        return new AuthException(ErrorCode.EMAIL_SEND_FAILED);
    }

    public static AuthException userNotFoundException() {
        return new AuthException(ErrorCode.USER_NOT_FOUND);
    }

    public static AuthException userNotAuthorizedException() {
        return new AuthException(ErrorCode.USER_NOT_AUTHORIZED);
    }

    public static AuthException emailVerificationExpiredException() {
        return new AuthException(ErrorCode.EMAIL_VERIFICATION_EXPIRED);
    }

    public static AuthException emailAlreadyVerifiedException() {
        return new AuthException(ErrorCode.EMAIL_ALREADY_VERIFIED);
    }

    public static AuthException invalidEmailVerificationTokenException() {
        return new AuthException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
    }

    public static AuthException emailVerificationRequiredException() {
        return new AuthException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);
    }

    public static AuthException emailAlreadyAcceptedException() {
        return new AuthException(ErrorCode.EMAIL_ALREADY_ACCEPTED);
    }

    public static AuthException emailNotMatchException(String message) {
        return new AuthException(ErrorCode.EMAIL_NOT_MATCH, message);
    }

    public static AuthException deleteUserException(String message) {
        return new AuthException(ErrorCode.LAST_ROOT_ADMIN_CANNOT_LEAVE, message);
    }
}
